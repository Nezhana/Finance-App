import { Schema, model } from "mongoose";
import { handleMongooseError } from "../helpers/index.js";

const groupSchema = new Schema(
    {
        referalCode: {
          type: String, 
          required: true,
          unique: true,
        },
        adminId: {
            type: Schema.Types.ObjectId, 
            ref: "User", 
            required: false,
        }
    },
    { versionKey: false, timestamps: true }
)

groupSchema.post("save", handleMongooseError);

const Group = model("group", groupSchema);

export { Group };