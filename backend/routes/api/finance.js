import express from "express";
import { actions } from "../../controllers/finance.js";
import { validateBody, authenticate } from "../../middlewares/index.js";
import { categorySchemas } from "../../models/category.js";
import { recordSchemas } from "../../models/record.js";

const router = express.Router();

router.post("/categories", authenticate, validateBody(categorySchemas.newCategorySchema), actions.addCategory);
router.get("/categories/all", authenticate, actions.getCategories);
router.delete("/categories", authenticate, actions.deleteCategory);

router.post("/records", authenticate, validateBody(recordSchemas.newRecordSchema), actions.addRecord);
router.get("/records/all", authenticate, actions.getRecords);
router.delete("/records", authenticate, actions.deleteRecord);

router.get("/balance/current", authenticate, actions.getCurrentMonthBalance);
router.get("/balance/current/categories", authenticate, actions.getCurrentMonthCategoriesBalance);
router.get("/balance/current/category", authenticate, actions.getCategoryDetails);

export default router;