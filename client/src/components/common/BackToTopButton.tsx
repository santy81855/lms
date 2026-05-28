import style from "./BackToTopButton.module.css"

type BackToTopProp = {
    divRef: React.RefObject<HTMLDivElement | null>
}


export default function BackToTop({ divRef, }: BackToTopProp) {

    return (
        <>
            <button onClick={() => {
                divRef.current?.scrollTo({
                    top: 0, behavior: "smooth"
                })
            }}>
                Back To Top
            </button>
        </>
    );
}
