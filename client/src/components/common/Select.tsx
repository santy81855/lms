type SelectProps = {
    options: string[];
    onChange:
        | React.ChangeEventHandler<HTMLSelectElement, HTMLSelectElement>
        | undefined;
};

function Select({ options, onChange }: SelectProps) {
    return (
        <select title="options select" onChange={onChange}>
            {options.map((option) => {
                return <option value={option}>{option}</option>;
            })}
        </select>
    );
}

export default Select;
