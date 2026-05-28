
import styles from "./CourseTypeSelect.module.css";
import type { Dispatch, SetStateAction } from "react";

type CourseTypeSelectProp = {
    setCourseType: Dispatch<SetStateAction<string>>
};


export function CourseTypeSelect({ setCourseType }: CourseTypeSelectProp) {
    return (
        <select onChange={(e) => setCourseType(e.target.value)}>
            <option value="All">All</option>
            <option value="Active">Active</option>
            <option value="Draft">Draft</option>
            <option value="Archived">Archived</option>
        </select>
    );
}
