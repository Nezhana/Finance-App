import dotenv from "dotenv";
import { Record } from "../models/record.js";
import { Category } from "../models/category.js";
import ctrlWrapper from "../helpers/CtrlWrapper.js";

dotenv.config();

const getStatistics = async (req, res) => {
    const records = await Record.find({ userId: req.user.id });
    const categories = await Category.find({ balanceId: req.params.balanceId });
    const totalSpent = records.reduce((acc, record) => acc + record.amount, 0);
    const totalIncome = records.filter(record => record.type === "income").reduce((acc, record) => acc + record.amount, 0);
    const totalEarnings = records.filter(record => record.type === "earnings").reduce((acc, record) => acc + record.amount, 0);
    const totalSavings = records.filter(record => record.type === "savings").reduce((acc, record) => acc + record.amount, 0);
    const totalExpenditure = records.filter(record => record.type === "expenditure").reduce((acc, record) => acc + record.amount, 0);
    const totalCategories = categories.length;
    res.status(200).json({
        totalSpent,
        totalIncome,
        totalEarnings,
        totalSavings,
        totalExpenditure,
        totalCategories,
    });
}


export const actions = {
  getStatistics: ctrlWrapper(getStatistics)
};