import { formatDistanceToNow, isValid } from "date-fns";

export function formatTimeAgo(dateInput: string | Date | undefined): string {
    if (dateInput === undefined) return "(No date info)";

    const date = new Date(dateInput);

    if (!isValid(date)) return "(Invalid date)";

    return formatDistanceToNow(date, {
        addSuffix: true,
        includeSeconds: true,
    });
}
