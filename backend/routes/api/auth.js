import express from "express";
import { actions } from "../../controllers/auth.js";
import { validateBody, authenticate } from "../../middlewares/index.js";
import { schemas } from "../../models/user.js";

const router = express.Router();

router.post("/register", validateBody(schemas.registerSchema), actions.register);
router.post("/login", validateBody(schemas.loginSchema), actions.login);
router.post("/logout", authenticate, actions.logout); 
router.get("/data", authenticate, actions.getUserData); 
router.put("/data", authenticate, actions.updateUserData); 
// router.get("/current", authenticate, actions.current); 
router.post("/password_recovery", actions.passwordRecovery);
router.delete("/", authenticate , actions.deleteUser); 

export default router;
