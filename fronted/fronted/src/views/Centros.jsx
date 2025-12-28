// src/views/Centros.jsx
import { useEffect, useState } from "react";
import { obtenerCentros, obtenerCentroPorId, obtenerPaquetesDeCentro, obtenerMensajerosDeCentro,} from "../service/centro";

export default function Centros() {
  const [centros, setCentros] = useState([]);
  const [loadingCentros, setLoadingCentros] = useState(true);
  const [errorCentros, setErrorCentros] = useState("");

  const [centroSeleccionadoId, setCentroSeleccionadoId] = useState(null);
  const [detalle, setDetalle] = useState(null);
  const [loadingDetalle, setLoadingDetalle] = useState(false);
  const [errorDetalle, setErrorDetalle] = useState("");

  const [paquetes, setPaquetes] = useState([]);
  const [loadingPaquetes, setLoadingPaquetes] = useState(false);
  const [errorPaquetes, setErrorPaquetes] = useState("");

  const [mensajeros, setMensajeros] = useState([]);
  const [loadingMensajeros, setLoadingMensajeros] = useState(false);
  const [errorMensajeros, setErrorMensajeros] = useState("");

  // Cargar lista de centros al entrar
  useEffect(() => {
    cargarCentros();
  }, []);

  const cargarCentros = async () => {
    try {
      setLoadingCentros(true);
      setErrorCentros("");
      const data = await obtenerCentros();
      setCentros(Array.isArray(data) ? data : []);
    } catch (e) {
      setErrorCentros(e.message || "Error al cargar centros");
    } finally {
      setLoadingCentros(false);
    }
  };

  const seleccionarCentro = async (id) => {
    setCentroSeleccionadoId(id);

    // reset de secciones
    setDetalle(null);
    setPaquetes([]);
    setMensajeros([]);
    setErrorDetalle("");
    setErrorPaquetes("");
    setErrorMensajeros("");

    try {
      setLoadingDetalle(true);
      const info = await obtenerCentroPorId(id);
      setDetalle(info);
    } catch (e) {
      setErrorDetalle(e.message || "Error al cargar detalle");
    } finally {
      setLoadingDetalle(false);
    }
  };

  const cargarPaquetes = async () => {
    if (!centroSeleccionadoId) return;
    try {
      setLoadingPaquetes(true);
      setErrorPaquetes("");
      const data = await obtenerPaquetesDeCentro(centroSeleccionadoId);
      setPaquetes(Array.isArray(data) ? data : []);
    } catch (e) {
      setErrorPaquetes(e.message || "Error al cargar paquetes");
    } finally {
      setLoadingPaquetes(false);
    }
  };

  const cargarMensajeros = async () => {
    if (!centroSeleccionadoId) return;
    try {
      setLoadingMensajeros(true);
      setErrorMensajeros("");
      const data = await obtenerMensajerosDeCentro(centroSeleccionadoId);
      setMensajeros(Array.isArray(data) ? data : []);
    } catch (e) {
      setErrorMensajeros(e.message || "Error al cargar mensajeros");
    } finally {
      setLoadingMensajeros(false);
    }
  };

  return (
    <section style={{ display: "grid", gap: 16 }}>
      <h2 style={{ margin: 0 }}>Gestión de Centros</h2>

      {/* LISTA */}
      <div style={{ display: "grid", gap: 10, padding: 12, border: "1px solid #f01414ff", borderRadius: 8 }}>
        <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center", gap: 12 }}>
          <strong>Centros</strong>
          <button onClick={cargarCentros} style={{ padding: "6px 10px" }}>
            Recargar
          </button>
        </div>

        {loadingCentros && <p>Cargando centros...</p>}
        {errorCentros && <p style={{ color: "crimson" }}>{errorCentros}</p>}

        {!loadingCentros && !errorCentros && centros.length === 0 && <p>No hay centros cargados.</p>}

        {!loadingCentros && centros.length > 0 && (
          <div style={{ display: "grid", gap: 8 }}>
            {centros.map((c) => (
              <button
                key={c.id}
                onClick={() => seleccionarCentro(c.id)}
                style={{
                    textAlign: "left",
                    padding: 10,
                    borderRadius: 8,
                    border: centroSeleccionadoId === c.id ? "2px solid #333" : "1px solid #f01d1dff",
                    background: centroSeleccionadoId === c.id ? "#47f811c4" : "white",
                    color: centroSeleccionadoId === c.id ? "white" : "#111",   // <-- ESTO
                    cursor: "pointer",
                    }}
              >
                <div><strong>{c.id}</strong> — {c.nombre}</div>
                <div style={{ fontSize: 13, opacity: 0.8 }}>{c.ciudad}</div>
              </button>
            ))}
          </div>
        )}
      </div>

      {/* DETALLE */}
      <div style={{ padding: 12, border: "1px solid #7ef013dc", borderRadius: 8 }}>
        <strong>Detalle del centro</strong>

        {!centroSeleccionadoId && <p style={{ marginTop: 8 }}>Seleccione un centro para ver detalle.</p>}

        {centroSeleccionadoId && loadingDetalle && <p style={{ marginTop: 8 }}>Cargando detalle...</p>}
        {centroSeleccionadoId && errorDetalle && <p style={{ marginTop: 8, color: "crimson" }}>{errorDetalle}</p>}

        {centroSeleccionadoId && !loadingDetalle && detalle && (
          <div style={{ marginTop: 10, display: "grid", gap: 6 }}>
            <div><strong>ID:</strong> {detalle.id}</div>
            <div><strong>Nombre:</strong> {detalle.nombre}</div>
            <div><strong>Ciudad:</strong> {detalle.ciudad}</div>

            <hr />

            <div><strong>Capacidad máxima:</strong> {detalle.capacidadMaxima ?? detalle.capacidad ?? "?"}</div>
            <div><strong>Carga actual:</strong> {detalle.cargaActual ?? "?"}</div>
            <div><strong>% uso:</strong> {detalle.porcentajeUso ?? "?"}</div>

            {Array.isArray(detalle.mensajerosIds) && (
              <div><strong>Mensajeros IDs:</strong> {detalle.mensajerosIds.join(", ") || "(ninguno)"}</div>
            )}

            <div style={{ display: "flex", gap: 10, marginTop: 10, flexWrap: "wrap" }}>
              <button onClick={cargarPaquetes} style={{ padding: "6px 10px" }}>
                Ver paquetes
              </button>
              <button onClick={cargarMensajeros} style={{ padding: "6px 10px" }}>
                Ver mensajeros
              </button>
            </div>
          </div>
        )}
      </div>

     {/* PAQUETES */}
{centroSeleccionadoId && (
  <div style={{ padding: 12, border: "1px solid #ddd", borderRadius: 8 }}>
    <strong>Paquetes del centro</strong>

    {loadingPaquetes && <p style={{ marginTop: 8 }}>Cargando paquetes...</p>}
    {errorPaquetes && <p style={{ marginTop: 8, color: "crimson" }}>{errorPaquetes}</p>}

    {!loadingPaquetes && !errorPaquetes && (
      paquetes.length === 0 ? (
        <p style={{ marginTop: 8 }}>No hay paquetes en este centro.</p>
      ) : (
        <div style={{ marginTop: 8, display: "grid", gap: 10 }}>
          {paquetes.map((p) => (
            <div
              key={p.id}
              style={{
                border: "1px solid #444",
                borderRadius: 10,
                padding: 12,
                background: "#1f1f1f",
              }}
            >
              <div style={{ display: "flex", justifyContent: "space-between" }}>
                <strong>ID: {p.id}</strong>
              </div>

              <div style={{ marginTop: 8, fontSize: 14, lineHeight: 1.5 }}>
                <div><b>Cliente:</b> {p.cliente}</div>
                <div><b>Centro actual:</b> {p.centroActual}</div>
                <div><b>Destino:</b> {p.destino}</div>
                <div><b>Peso:</b> {p.peso} kg</div>
                 <div><b>Estado:</b> {p.estado}</div>
              </div>
            </div>
          ))}
        </div>
      )
    )}
  </div>
)}

      {/* MENSAJEROS */}
{centroSeleccionadoId && (
  <div style={{ padding: 12, border: "1px solid #cc2c2cff", borderRadius: 8 }}>
    <strong>Mensajeros del centro</strong>

    {loadingMensajeros && <p style={{ marginTop: 8 }}>Cargando mensajeros...</p>}
    {errorMensajeros && (
      <p style={{ marginTop: 8, color: "crimson" }}>{errorMensajeros}</p>
    )}

    {!loadingMensajeros && !errorMensajeros && (
      <div style={{ marginTop: 8 }}>
        {mensajeros.length === 0 ? (
          <p>No hay mensajeros en este centro.</p>
        ) : (
          mensajeros.map((m) => (
            <div key={m.id} style={{ marginBottom: 12 }}>
              <div><b>ID:</b> {m.id}</div>
              <div><b>Nombre:</b> {m.nombre}</div>
              <div><b>Capacidad:</b> {m.capacidad}</div>
              <div><b>Centro:</b> {m.centroId ?? m.centro}</div>
              <div><b>Estado:</b> {m.estado}</div>

              <hr style={{ margin: "10px 0", opacity: 0.3 }} />
            </div>
          ))
        )}
      </div>
    )}
  </div>
)}

</section>
);
}