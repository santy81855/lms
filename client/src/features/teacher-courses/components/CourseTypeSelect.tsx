import type { Dispatch, SetStateAction } from "react";
import Select from "@/components/common/Select";

type CourseTypeSelectProp = {
    setCourseType: Dispatch<SetStateAction<string>>;
};

export function CourseTypeSelect({ setCourseType }: CourseTypeSelectProp) {
    const options = ["All", "Active", "Draft", "Archived"];

    const handleOptionClick = (e: React.ChangeEvent<HTMLSelectElement>) => {
        setCourseType(e.currentTarget.value);
    };

    return <Select options={options} onChange={handleOptionClick} />;
}
