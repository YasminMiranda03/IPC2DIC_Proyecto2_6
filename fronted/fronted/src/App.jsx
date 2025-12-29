import { Routes, Route } from "react-router-dom";
import NavBar from "./components/Navbar";
import ImportarXml from "./views/ImportarXml";
import Centros from "./views/Centros";
import Rutas from "./views/Rutas";
import Mensajeros from "./views/Mensajeros";
import Solicitudes from "./views/Solicitudes";
import Paquetes from "./views/Paquete";
export default function App() {
  return (
    <div>
      <NavBar />
      <main style={{ padding: 24 }}>
        <Routes>
          <Route path="/" element={<h1>IPC2 Proyecto</h1>} />
          <Route path="/importar" element={<ImportarXml />} />
          <Route path="/centros" element={<Centros />} />
          <Route path="/ruta" element={<Rutas/>}/>
          <Route path="/mensajeros" element={<Mensajeros />} />
          <Route path="/solicitudes" element={<Solicitudes/>} />
          <Route path="/paquetes" element={<Paquetes/>} />
        </Routes>
      </main>
    </div>
  );
}