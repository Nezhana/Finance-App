import { Schema, model } from "mongoose";
import { handleMongooseError } from "../helpers/index.js";
import Joi from "joi";

const recordSchema = new Schema(
    {
        balanceId: {
          type: Schema.Types.ObjectId, 
          ref: "Balance", 
          required: true,
        },
        type: {
            type: String,
            enum: ["income", "expense"],
            default: "expense",
        },
        title: {
            type: String,
            required: [true, "Set title for record"],
        },
        date: {
          type: Date,
          required: [true, "Set date for record"],
        },
        value: {
            type: Number,
            required: [true, "Set value for record"],
            min: 0,
        },
        method: {
            type: String,
            enum: ["cash", "card"],
            default: "cash",
        },
        categoryId: {
            type: Schema.Types.ObjectId,
            ref: "Category",
            required: true,
        },
        reccurent: {
            type: Boolean,
            default: false,
        },
        repeating: {
            type: String,
            enum: ["daily", "weekly", "monthly", "yearly"],
            default: null,
        }
    },
    { versionKey: false, timestamps: true }
)

recordSchema.post("save", handleMongooseError);

const newRecordSchema = Joi.object({
  type: Joi.string().valid("income", "expense").default("expense"),
  title: Joi.string().required().messages({
    "string.empty": "Set title for record",
  }),
  value: Joi.number().min(0).required().messages({
    "number.base": "Value must be a number",
    "number.min": "Value must be at least 0",
  }),
  date: Joi.date().required().messages({
    "any.required": "Set date for record",
  }),
  method: Joi.string().valid("cash", "card").default("cash"),
  categoryId: Joi.string().hex().length(24).required(), 
  reccurent: Joi.boolean().default(false),
  repeating: Joi.string().valid("daily", "weekly", "monthly", "yearly").allow(null).default(null),
});

const recordSchemas = {
  newRecordSchema
};

const Record = model("record", recordSchema);

export { Record, recordSchemas };