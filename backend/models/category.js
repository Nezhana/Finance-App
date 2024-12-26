import { Schema, model } from "mongoose";
import { handleMongooseError } from "../helpers/index.js";
import Joi from "joi";

const categorySchema = new Schema(
    {
        title: {
          type: String, 
          required: [true, "Set name for category"],
        },
        balanceId: {
            type: Schema.Types.ObjectId,
            ref: "Balance", 
            required: true,
        }
    },
    { versionKey: false, timestamps: true }
)

categorySchema.post("save", handleMongooseError);

const newCategorySchema = Joi.object({
  title: Joi.string().required()
});

const categorySchemas = {
  newCategorySchema
};

const Category = model("category", categorySchema);

export { Category, categorySchemas };