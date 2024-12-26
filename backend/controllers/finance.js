import dotenv from "dotenv";
import { User } from "../models/user.js";
import { Group } from "../models/group.js";
import { Balance } from "../models/balance.js";
import { Category } from "../models/category.js";
import ctrlWrapper from "../helpers/CtrlWrapper.js";
import { Record } from "../models/record.js";
import HttpError from "../helpers/HttpError.js";

dotenv.config();

const addCategory = async (req, res) => {
  const balanceId = await getBalanceId(req.user.token);
  await Category.create({ title: req.body.title, balanceId: balanceId._id });
  res.status(201).json({ message: "Category added successfully" });
}

const getCategories = async (req, res) => {
  const balanceId = await getBalanceId(req.user.token);
  const categories = await Category.find({ balanceId: balanceId._id });
  res.status(200).json({ categories });
}

const deleteCategory = async (req, res) => {
  if(!req.query.id) throw new HttpError(400, "Missing required parameter: id");
  const balanceId = await getBalanceId(req.user.token);
  const otherCategory = await Category.findOne({ title: "other", balanceId });
  await Record.updateMany({ categoryId: req.query.id }, { categoryId: otherCategory.id });
  await Category.findByIdAndDelete({ _id: req.query.id });
  res.status(204).end();
}

const addRecord = async (req, res) => {
  const balanceId = await getBalanceId(req.user.token);
  const editedDate = new Date(req.body.date);
  await Record.create({ ...req.body, balanceId: balanceId._id, date: editedDate });
  res.status(201).json({ message: "Record added successfully" });
}

const getRecords = async (req, res) => {
  const balanceId = await getBalanceId(req.user.token);
  const records = await Record.find({ balanceId: balanceId });
  res.status(200).json({ records });
}

const deleteRecord = async (req, res) => {
  if (!req.query.id) throw new HttpError(400, "Missing required parameter: id");
  await Record.findByIdAndDelete({ _id: req.query.id });
  res.status(204).end();
}

export const getBalanceId = async (token) => {
  const userCode = await User.findOne({ token }).select({ referalCode: 1 });
  const groupId = await Group.findOne({ referalCode: userCode.referalCode }).select({ _id: 1 });
  const balanceId = await Balance.findOne({ groupId: groupId._id }).select({ _id: 1 });
  return balanceId._id;
}

const getCurrentMonthBalance = async (req, res) => {
  const balanceId = await getBalanceId(req.user.token);
  const balance = await Balance.findById(balanceId).select("currency");

  const now = new Date();
  const startOfMonth = new Date(now.getFullYear(), now.getMonth(), 1);
  const endOfMonth = new Date(now.getFullYear(), now.getMonth() + 1, 0);

  const [incomeTotal, expenseTotal] = await Promise.all([
    Record.aggregate([
      {
        $match: {
          balanceId: balanceId,
          type: "income",
          date: { $gte: startOfMonth, $lte: endOfMonth },
        },
      },
      {
        $group: {
          _id: null,
          total: { $sum: "$value" },
        },
      },
    ]),
    Record.aggregate([
      {
        $match: {
          balanceId: balanceId,
          type: "expense",
          date: { $gte: startOfMonth, $lte: endOfMonth },
        },
      },
      {
        $group: {
          _id: null,
          total: { $sum: "$value" },
        },
      },
    ]),
  ]);

  const totalIncome = incomeTotal.length > 0 ? incomeTotal[0].total : 0;
  const totalExpense = expenseTotal.length > 0 ? expenseTotal[0].total : 0;
  const totalBalance = totalIncome - totalExpense;

  res.status(200).json({
    currency: balance.currency,
    currentMonth: now.getMonth() + 1,
    incomeTotal: totalIncome,
    expenseTotal: totalExpense,
    total: totalBalance,
  });
};

const getCurrentMonthCategoriesBalance = async (req, res) => {
  const balanceId = await getBalanceId(req.user.token);
  const balance = await Balance.findById(balanceId).select("currency");

  const now = new Date();
  const startOfMonth = new Date(now.getFullYear(), now.getMonth(), 1);
  const endOfMonth = new Date(now.getFullYear(), now.getMonth() + 1, 0);

  const userCategories = await Category.find({ balanceId }).select({ title: 1, _id: 1 });
  
  const categoriesRecords = await Promise.all(
    userCategories.map(async (category) => ({
      title: category.title,
      total: await Record.aggregate([
        {
          $match: {
            balanceId,
            categoryId: category._id,
            date: { $gte: startOfMonth, $lte: endOfMonth },
          },
        },
        {
          $group: {
            _id: null,
            total: { $sum: "$value" },
          },
        },
      ]).then((result) => result.length > 0? result[0].total : 0),
      categoryId: category._id
    }))
  );

  res.status(200).json({ currency: balance.currency, month: now.getMonth() + 1, categories: categoriesRecords });
}

const getCategoryDetails = async (req, res) => {
  const categoryId = req.query.categoryId;
  if (!categoryId) {
    res.status(400).json({ message: "Category ID is required" });
    return;
  }
  const balanceId = await getBalanceId(req.user.token);
  const balance = await Balance.findById(balanceId).select("currency");

  const now = new Date();
  const startOfMonth = new Date(now.getFullYear(), now.getMonth(), 1);
  const endOfMonth = new Date(now.getFullYear(), now.getMonth() + 1, 0);

  const userCategory = await Category.findById(categoryId).select({ title: 1, _id: 1 });
  const records = await Record.find({ balanceId, categoryId, date: { $gte: startOfMonth, $lte: endOfMonth } }).select({ title: 1, value: 1, date: 1, type: 1 });

  const total = records.reduce((acc, record) => record.type === 'income' ? acc + record.value : acc - record.value, 0);

  res.status(200).json({
    category: {
      title: userCategory.title,
      total,
      currency: balance.currency,
      records
    }
  });
}


export const actions = {
  addCategory: ctrlWrapper(addCategory),
  getCategories: ctrlWrapper(getCategories),
  deleteCategory: ctrlWrapper(deleteCategory),
  addRecord: ctrlWrapper(addRecord),
  getRecords: ctrlWrapper(getRecords),
  deleteRecord: ctrlWrapper(deleteRecord),
  getCurrentMonthBalance: ctrlWrapper(getCurrentMonthBalance),
  getCurrentMonthCategoriesBalance: ctrlWrapper(getCurrentMonthCategoriesBalance),
  getCategoryDetails: ctrlWrapper(getCategoryDetails),
};