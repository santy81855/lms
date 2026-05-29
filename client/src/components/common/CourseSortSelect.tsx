import type { Dispatch, SetStateAction } from "react";
import Select from "@/components/common/Select";

type CourseSortSelectProp = {
    setSort: Dispatch<SetStateAction<string>>;
};

export function CourseSortSelect({ setSort }: CourseSortSelectProp) {
    const options = ["A-Z", "Newest", "Oldest"];

    const handleOptionClick = (e: React.ChangeEvent<HTMLSelectElement>) => {
        setSort(e.currentTarget.value);
    };

    return <Select options={options} onChange={handleOptionClick} />;
}
