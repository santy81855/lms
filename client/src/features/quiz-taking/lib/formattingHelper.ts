export function formatTimeAgo(dateInput: string | Date | undefined): string {
    if (dateInput === undefined) return "(No date info)";
    const date = new Date(dateInput);
    const now = new Date();

    const diffMs = now.getTime() - date.getTime();

    const seconds = Math.floor(diffMs / 1000);
    const minutes = Math.floor(seconds / 60);
    const hours = Math.floor(minutes / 60);
    const days = Math.floor(hours / 24);

    if (seconds < 10) return "just now";
    if (seconds < 60) return `${seconds} seconds ago`;

    if (minutes < 60) {
        return minutes === 1 ? "1 minute ago" : `${minutes} minutes ago`;
    }

    if (hours < 24) {
        return hours === 1 ? "1 hour ago" : `${hours} hours ago`;
    }

    return days === 1 ? "1 day ago" : `${days} days ago`;
}
