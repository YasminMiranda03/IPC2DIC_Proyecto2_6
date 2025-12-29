import { useEffect, useMemo, useState } from "react";
import {
  obtenerMensajeros,
  obtenerMensajeroPorId,
  crearMensajero,
  cambiarEstadoMensajero,
  reasignarCentroMensajero,
  asignarEnvioDirecto, 
} from "../service/mensajerosService";

export default function Mensajeros() {
  const [mensajeros, setMensajeros] = useState([]);
  const [loadingLista, setLoadingLista] = useState(true);
  const [errorLista, setErrorLista] = useState("");

  const [seleccionId, setSeleccionId] = useState("");
  const [detalle, setDetalle] = useState(null);
  const [loadingDetalle, setLoadingDetalle] = useState(false);
  const [errorDetalle, setErrorDetalle] = useState("");

  const [nuevo, setNuevo] = useState({
    id: "",
    nombre: "",
    capacidad: "",
    centroId: "",
  });

  const [paqueteIdAsignar, setPaqueteIdAsignar] = useState(""); 

  const [msgOk, setMsgOk] = useState("");
  const [msgErr, setMsgErr] = useState("");

  const limpiarMensajes = () => {
    setMsgOk("");
    setMsgErr("");
  };

  const recargarLista = async () => {
    try {
      setLoadingLista(true);
      setErrorLista("");
      const data = await obtenerMensajeros();
      setMensajeros(Array.isArray(data) ? data : []);
    } catch (e) {
      setErrorLista(e.message || "Error al cargar mensajeros");
      setMensajeros([]);
    } finally {
      setLoadingLista(false);
    }
  };


  useEffect(() => {
    recargarLista();
  }, []);

  const seleccionar = async (id) => {
    limpiarMensajes();
    setSeleccionId(id);
    setDetalle(null);
    setErrorDetalle("");

    try {
      setLoadingDetalle(true);
      const data = await obtenerMensajeroPorId(id);
      setDetalle(data);
    } catch (e) {
      setErrorDetalle(e.message || "Error al cargar detalle");
    } finally {
      setLoadingDetalle(false);
    }
  };

  const seleccionado = useMemo(() => {
    if (!seleccionId) return null;
    return mensajeros.find((m) => m?.id === seleccionId) || null;
  }, [mensajeros, seleccionId]);

  const crear = async (e) => {
    e.preventDefault();
    limpiarMensajes();

    if (!nuevo.id || !nuevo.nombre || !nuevo.centroId) {
      setMsgErr("Complete: id, nombre y centroId.");
      return;
    }

    const cap = Number(nuevo.capacidad);
    if (!Number.isFinite(cap) || cap <= 0) {
      setMsgErr("La capacidad debe ser un número > 0.");
      return;
    }

    try {
      await crearMensajero({
        id: nuevo.id.trim(),
        nombre: nuevo.nombre.trim(),
        centroId: nuevo.centroId.trim(),
        capacidad: cap,
        estado: "DISPONIBLE",
      });

      setMsgOk("Mensajero creado.");
      setNuevo({ id: "", nombre: "", capacidad: "", centroId: "" });
      await recargarLista();
    } catch (e2) {
      setMsgErr(e2.message || "Error creando mensajero");
    }
  };

  const cambiarEstado = async (estado) => {
    if (!seleccionId) return;
    limpiarMensajes();

    if (estado !== "DISPONIBLE" && estado !== "EN_TRANSITO") {
      setMsgErr("Estado inválido. Use DISPONIBLE o EN_TRANSITO.");
      return;
    }

    try {
      await cambiarEstadoMensajero(seleccionId, estado);
      setMsgOk(`Estado cambiado a ${estado}.`);
      await recargarLista();
      await seleccionar(seleccionId);
    } catch (e) {
      setMsgErr(e.message || "No se pudo cambiar el estado");
    }
  };

  const reasignarCentro = async () => {
    if (!seleccionId) return;
    limpiarMensajes();

    const estadoActual = (detalle?.estado ?? seleccionado?.estado ?? "").toUpperCase();
    if (estadoActual === "EN_TRANSITO") {
      setMsgErr("No se puede reasignar: el mensajero está EN_TRANSITO.");
      return;
    }

    const nuevoCentroId = prompt("Nuevo centro ID:");
    if (!nuevoCentroId) return;

    try {
      await reasignarCentroMensajero(seleccionId, nuevoCentroId.trim());
      setMsgOk("Centro reasignado.");
      await recargarLista();
      await seleccionar(seleccionId);
    } catch (e) {
      setMsgErr(e.message || "No se pudo reasignar el centro");
    }
  };

 
  const asignarDirecto = async () => {
    if (!seleccionId) return;
    limpiarMensajes();

    const pid = paqueteIdAsignar.trim();
    if (!pid) {
      setMsgErr("Ingrese el ID del paquete (ej: P001).");
      return;
    }

    const estadoActual = (detalle?.estado ?? seleccionado?.estado ?? "").toUpperCase();
    if (estadoActual !== "DISPONIBLE") {
      setMsgErr("El mensajero debe estar DISPONIBLE para asignar un paquete.");
      return;
    }

    try {
      await asignarEnvioDirecto(pid, seleccionId);
      setMsgOk(`   Asignado: ${pid} → ${seleccionId}`);
      setPaqueteIdAsignar("");

      await recargarLista();
      await seleccionar(seleccionId);
    } catch (e) {
      setMsgErr(e.message || "No se pudo asignar el envío.");
    }
  };

  return (
    <section style={{ display: "grid", gap: 16 }}>
      <h2 style={{ margin: 0 }}>Gestión de Mensajeros</h2>

      {(msgOk || msgErr) && (
        <div
          style={{
            padding: 10,
            borderRadius: 8,
            border: "1px solid #ddd",
            background: msgErr ? "#ffe9e9" : "#e9fff0",
            color: msgErr ? "#a10000" : "#0b5f2a",
          }}
        >
          {msgErr || msgOk}
        </div>
      )}
      <div style={{ display: "grid", gridTemplateColumns: "1fr 1.2fr", gap: 16 }}>
   
        <div style={{ padding: 12, border: "1px solid #ddd", borderRadius: 10 }}>
          <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center", gap: 10 }}>
            <strong>Mensajeros</strong>
          
          </div>

          {loadingLista && <p style={{ marginTop: 10 }}>Cargando...</p>}
          {errorLista && <p style={{ marginTop: 10, color: "crimson" }}>{errorLista}</p>}

          {!loadingLista && !errorLista && mensajeros.length === 0 && (
            <p style={{ marginTop: 10 }}>No hay mensajeros cargados.</p>
          )}

          {!loadingLista && mensajeros.length > 0 && (
            <div style={{ marginTop: 10, display: "grid", gap: 8 }}>
              {mensajeros.map((m) => (
                <button
                  key={m.id}
                  onClick={() => seleccionar(m.id)}
                  style={{
                    textAlign: "left",
                    padding: 10,
                    borderRadius: 10,
                    border: seleccionId === m.id ? "2px solid #333" : "1px solid #ddd",
                    background: seleccionId === m.id ? "#111" : "white",
                    color: seleccionId === m.id ? "white" : "#111",
                    cursor: "pointer",
                  }}
                >
                  <div style={{ display: "flex", justifyContent: "space-between", gap: 10 }}>
                    <div>
                      <strong>{m.id}</strong> — {m.nombre}
                    </div>
                    <span style={{ fontSize: 12, opacity: 0.9 }}>{m.estado}</span>
                  </div>
                  <div style={{ fontSize: 13, opacity: 0.8 }}>
                    Centro: {m.centroId} · Capacidad: {m.capacidad}
                  </div>
                </button>
              ))}
            </div>
          )}
        </div>

        <div style={{ padding: 12, border: "1px solid #ddd", borderRadius: 10 }}>
          <strong>Detalle</strong>

          {!seleccionId && <p style={{ marginTop: 10 }}>Seleccione un mensajero.</p>}

          {seleccionId && loadingDetalle && <p style={{ marginTop: 10 }}>Cargando detalle...</p>}
          {seleccionId && errorDetalle && <p style={{ marginTop: 10, color: "crimson" }}>{errorDetalle}</p>}

          {seleccionId && !loadingDetalle && detalle && (
            <div style={{ marginTop: 10, display: "grid", gap: 6 }}>
              <div>
                <strong>ID:</strong> {detalle.id}
              </div>
              <div>
                <strong>Nombre:</strong> {detalle.nombre}
              </div>
              <div>
                <strong>Centro actual:</strong> {detalle.centroId}
              </div>
              <div>
                <strong>Capacidad:</strong> {detalle.capacidad}
              </div>
              <div>
                <strong>Estado:</strong>{" "}
                <span style={{ fontWeight: "bold" }}>{detalle.estado}</span>
              </div>

              <hr style={{ margin: "10px 0" }} />

              <div style={{ display: "flex", gap: 10, flexWrap: "wrap" }}>
                <button
                  onClick={() => cambiarEstado("EN_TRANSITO")}
                  disabled={detalle.estado === "EN_TRANSITO"}
                  style={{ padding: "6px 10px" }}
                >
                  Poner EN_TRANSITO
                </button>

                <button
                  onClick={() => cambiarEstado("DISPONIBLE")}
                  disabled={detalle.estado === "DISPONIBLE"}
                  style={{ padding: "6px 10px" }}
                >
                  Poner DISPONIBLE
                </button>

                <button
                  onClick={reasignarCentro}
                  disabled={detalle.estado === "EN_TRANSITO"}
                  style={{ padding: "6px 10px" }}
                  title={detalle.estado === "EN_TRANSITO" ? "No permitido en tránsito" : ""}
                >
                  Reasignar centro
                </button>
              </div>

              
              <hr style={{ margin: "12px 0" }} />
              <strong>Asignación directa (paquete → este mensajero)</strong>

              <div style={{ display: "flex", gap: 10, marginTop: 8, flexWrap: "wrap" }}>
                <input
                 
                  value={paqueteIdAsignar}
                  onChange={(e) => setPaqueteIdAsignar(e.target.value)}
                  style={{ minWidth: 220 }}
                />
                <button onClick={asignarDirecto} style={{ padding: "6px 10px" }}>
                  Asignar paquete a {detalle.id}
                </button>
              </div>

              <div style={{ fontSize: 12, opacity: 0.75, marginTop: 6 }}>
              </div>
            </div>
          )}
        </div>
      </div>

      <div style={{ padding: 12, border: "1px solid #ddd", borderRadius: 10 }}>
        <strong>Crear mensajero</strong>

        <form onSubmit={crear} style={{ marginTop: 10, display: "grid", gap: 10 }}>
          <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr", gap: 10 }}>
            <input
        
              value={nuevo.id}
              onChange={(e) => setNuevo({ ...nuevo, id: e.target.value })}
            />
            <input
              
              value={nuevo.nombre}
              onChange={(e) => setNuevo({ ...nuevo, nombre: e.target.value })}
            />
            <input
            
              type="number"
              value={nuevo.capacidad}
              onChange={(e) => setNuevo({ ...nuevo, capacidad: e.target.value })}
            />
            <input
             
              value={nuevo.centroId}
              onChange={(e) => setNuevo({ ...nuevo, centroId: e.target.value })}
            />
          </div>

          <div>
            <button type="submit" style={{ padding: "6px 10px" }}>
              Guardar
            </button>
          </div>
        </form>
      </div>
    </section>
  );
}