import { Schema, model } from "mongoose";
import { handleMongooseError } from "../helpers/index.js";

const balanceSchema = new Schema(
    {
        groupId: {
            type: Schema.Types.ObjectId,
            ref: "Group",
            required: true,
        },
        currency: {
            type: String,
            required: true,
        }
    },
    { versionKey: false, timestamps: true }
)

balanceSchema.post("save", handleMongooseError);

const Balance = model("balance", balanceSchema);

export { Balance };