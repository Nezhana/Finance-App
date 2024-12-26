import ctrlWrapper from "../helpers/CtrlWrapper.js";

const { EXCHANGE_RATE_URL, EXCHANGE_RATE_API_KEY } = process.env;

const getCurrency = async (req, res) => {
  const response = await fetch(`${EXCHANGE_RATE_URL}/symbols?access_key=${EXCHANGE_RATE_API_KEY}`);
  const data = await response.json(); 
  res.status(200).json(data);
}

const getMainCurrencyRates = async (req, res) => {
  const response = await fetch(`${EXCHANGE_RATE_URL}/latest?access_key=${EXCHANGE_RATE_API_KEY}&symbols=USD,EUR,UAH`);
  const data = await response.json(); 
  res.status(200).json(data);
}

const getCurrencyRates = async (req, res) => {
  const response = await fetch(`${EXCHANGE_RATE_URL}/latest?access_key=${EXCHANGE_RATE_API_KEY}`);
  const data = await response.json(); 
  res.status(200).json(data);
}

export const actions = {
  getCurrency: ctrlWrapper(getCurrency),
  getMainCurrencyRates: ctrlWrapper(getMainCurrencyRates),
  getCurrencyRates: ctrlWrapper(getCurrencyRates),
};