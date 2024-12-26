import { Schema, model } from "mongoose";
import Joi from "joi";
import { handleMongooseError } from "../helpers/index.js";

const userSchema = new Schema(
  {
    name: {
      type: String,
      required: [true, "Set name for user"],
    },
    email: { //login
      type: String,
      required: [true, "Email is required (example@example.com)"],
      unique: true,
    },
    password: {
      type: String,
      required: [true, "Set password for user (examplepassword)"],
      minlength: 6,
    },
    referalCode: {
      type: String,
      required: [false]
    },
    role: {
      type: String,
      enum: ["admin", "user"],
      default: "user",
    },
    currency: {
      type: String,
      required: [true, "Set currency for account"],
    },
    token: {
      type: String,
    }
  },
  { versionKey: false, timestamps: true }
);

userSchema.post("save", handleMongooseError);

const registerSchema = Joi.object({
  name: Joi.string().required(),
  email: Joi.string().required(),
  password: Joi.string().min(6).required(),
  currency: Joi.string().required(),
  referalCode: Joi.string(),
});

const loginSchema = Joi.object({
  email: Joi.string().required(),
  password: Joi.string().min(6).required(),
});

const schemas = {
  registerSchema,
  loginSchema,
};

const User = model("user", userSchema);

export { User, schemas };