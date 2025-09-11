import "tailwindcss";

type NoteProps = {
    title: string;
    content: string;
};

function Note ({title, content}: NoteProps) {
    return (
        <div className="border p-4 m-2 rounded bg-white shadow">
            <h2 className="font-bold text-lg mb-2">{title}</h2>
            <p>{content}</p>
        </div>
    );
}

export default Note;