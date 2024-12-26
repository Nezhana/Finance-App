import express from "express";
import { authenticate } from "../../middlewares/index.js";
import { actions } from "../../controllers/group.js";

const router = express.Router();

router.get("/users", authenticate, actions.getGroupUsers);
router.delete("/users", authenticate, actions.deleteUserFromGroup);

export default router;