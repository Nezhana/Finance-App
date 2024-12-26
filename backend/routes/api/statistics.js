import express from "express";
import { authenticate } from "../../middlewares/index.js";
import { actions } from "../../controllers/statistics.js";

const router = express.Router();

router.get("/", authenticate, actions.getStatistics);

export default router;