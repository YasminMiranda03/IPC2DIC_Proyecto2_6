import { useState } from "react" 
import { importarXml } from "../service/importar" 


export default function ImportarXml() {
  const [file, setFile] = useState(null) 
  const [resultado, setResultado] = useState("") 

  const handleEnviar = async () => {
    try {
      const texto = await importarXml(file) 
      setResultado(texto) 
    } catch (e) {
      setResultado("ERROR: " + e.message) 
    }
  } 

  return (
    <div>
      <input
        type="file"
        accept=".xml"
        onChange={(e) => setFile(e.target.files?.[0] || null)}
      />
      <button onClick={handleEnviar} disabled={!file}>
        Enviar
      </button>

      <pre>{resultado}</pre>
    </div>
  ) 
}