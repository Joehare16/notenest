
import Note from './components/Note'
function App() {
  const notes  = [
      { id: 1, title: "Shopping List", content: "Milk, Eggs, Bread" },
  { id: 2, title: "Todo", content: "Finish NoteNest frontend" },
  { id: 3, title: "Ideas", content: "Build SaaS product" },
];

  return (
    <div className = "p-6 bg-gray=100 min-h-screen">
      <h1 className ="text-2x1 font-bold mb-4">My Notes</h1>
      {notes.map((note) => (
        <Note key={note.id} title={note.title} content={note.content} />
      ))}
    </div>
  );
}

export default App
