import { Routes, Route } from "react-router-dom";
import NavBar from "./components/Navbar";
import ImportarXml from "./views/ImportarXml";

export default function App() {
  return (
    <div>
      <NavBar />
      <main style={{ padding: 24 }}>
        <Routes>
          <Route path="/" element={<h1>LogiTrack - HT3</h1>} />
          <Route path="/importar" element={<ImportarXml />} />
        </Routes>
      </main>
    </div>
  );
}