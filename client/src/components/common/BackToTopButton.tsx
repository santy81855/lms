import styles from "./BackToTopButton.module.css";

type BackToTopProp = {
    divRef: React.RefObject<HTMLDivElement | null>;
};

export default function BackToTop({ divRef }: BackToTopProp) {
    return (
        <button
            className={styles.button}
            aria-label="Scroll to top"
            onClick={() => {
                divRef.current?.scrollTo({
                    top: 0,
                    behavior: "smooth",
                });
            }}
        >
            ↑
        </button>
    );
}
