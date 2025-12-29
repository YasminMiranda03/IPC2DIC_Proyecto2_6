import { useState } from "react";
import { exportarXml } from "../service/exportar";

export default function Exportar() {
  const [xml, setXml] = useState("");
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  const exportar = async () => {
    setError("");
    setXml("");

    try {
      setLoading(true);
      const data = await exportarXml();
      setXml(data);
    } catch (e) {
      setError(e.message || "Error al exportar XML");
    } finally {
      setLoading(false);
    }
  };

  const descargar = () => {
    if (!xml) return;

    const blob = new Blob([xml], { type: "application/xml" });
    const url = URL.createObjectURL(blob);

    const a = document.createElement("a");
    a.href = url;
    a.download = "resultadoCloudSync.xml";
    a.click();

    URL.revokeObjectURL(url);
  };

  return (
    <section style={{ display: "grid", gap: 16 }}>
      <h2>Exportar Resultado XML</h2>

      {error && (
        <div
          style={{
            color: "#a10000",
            background: "#ffe9e9",
            padding: 10,
            borderRadius: 6,
          }}
        >
          {error}
        </div>
      )}

      <button
        onClick={exportar}
        disabled={loading}
        style={{ width: 200 }}
      >
        {loading ? "Exportando..." : "Exportar XML"}
      </button>

      <button
        onClick={descargar}
        disabled={!xml}
        style={{ width: 200 }}
      >
        Descargar XML
      </button>
    </section>
  );
}
