import express from "express";
import { actions } from "../../controllers/currency.js";

const router = express.Router();

router.get("/", actions.getCurrency);
router.get("/rates/main", actions.getMainCurrencyRates); 
router.get("/rates", actions.getCurrencyRates); 

export default router;