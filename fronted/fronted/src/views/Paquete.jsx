import { useEffect, useMemo, useState } from "react";
import {
  obtenerPaquetes,
  obtenerPaquetePorId,
  crearPaquete,
  actualizarPaquete,
  eliminarPaquete,
} from "../service/paqueteService";

const ESTADOS_VALIDOS = ["PENDIENTE", "EN_TRANSITO", "ENTREGADO"];
const normEstado = (x) => (x ?? "").toString().trim().toUpperCase();

export default function Paquetes() {
  const [paquetes, setPaquetes] = useState([]);
  const [loadingLista, setLoadingLista] = useState(true);
  const [errorLista, setErrorLista] = useState("");

  const [seleccionId, setSeleccionId] = useState("");
  const [detalle, setDetalle] = useState(null);
  const [loadingDetalle, setLoadingDetalle] = useState(false);
  const [errorDetalle, setErrorDetalle] = useState("");

  const [nuevo, setNuevo] = useState({
    id: "",
    cliente: "",
    peso: "",
    destino: "",
    centroActual: "",
    estado: "PENDIENTE",
  });

  const [editar, setEditar] = useState({
    cliente: "",
    peso: "",
    destino: "",
    centroActual: "",
    estado: "",
  });

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
      const data = await obtenerPaquetes();
      setPaquetes(Array.isArray(data) ? data : []);
    } catch (e) {
      setErrorLista(e.message || "Error al cargar paquetes");
      setPaquetes([]);
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
      const data = await obtenerPaquetePorId(id);
      setDetalle(data);
      setEditar({
        cliente: data?.cliente ?? "",
        peso: data?.peso ?? "",
        destino: data?.destino ?? "",
        centroActual: data?.centroActual ?? "",
        estado: data?.estado ?? "",
      });
    } catch (e) {
      setErrorDetalle(e.message || "Error al cargar detalle");
    } finally {
      setLoadingDetalle(false);
    }
  };

  const seleccionado = useMemo(() => {
    if (!seleccionId) return null;
    return paquetes.find((p) => p?.id === seleccionId) || null;
  }, [paquetes, seleccionId]);

  const crear = async (e) => {
    e.preventDefault();
    limpiarMensajes();

    const id = nuevo.id.trim();
    const cliente = nuevo.cliente.trim();
    const destino = nuevo.destino.trim();
    const centroActual = nuevo.centroActual.trim();
    const estado = normEstado(nuevo.estado);

    if (!id || !cliente || !destino || !centroActual) {
      setMsgErr("Complete: id, cliente, destino y centroActual.");
      return;
    }

    const pesoNum = Number(nuevo.peso);
    if (!Number.isFinite(pesoNum) || pesoNum <= 0) {
      setMsgErr("El peso debe ser un número estrictamente > 0.");
      return;
    }

    if (estado && !ESTADOS_VALIDOS.includes(estado)) {
      setMsgErr("Estado inválido. Use: PENDIENTE, EN_TRANSITO, ENTREGADO.");
      return;
    }

    try {
      await crearPaquete({
        id,
        cliente,
        peso: pesoNum,
        destino,
        centroActual,
        estado: estado || "PENDIENTE",
      });

      setMsgOk("Paquete creado.");
      setNuevo({
        id: "",
        cliente: "",
        peso: "",
        destino: "",
        centroActual: "",
        estado: "PENDIENTE",
      });
      await recargarLista();
    } catch (e2) {
      setMsgErr(e2.message || "Error creando paquete");
    }
  };

  const guardarCambios = async () => {
    if (!seleccionId) return;
    limpiarMensajes();

    const payload = {};
    if (editar.cliente?.trim()) payload.cliente = editar.cliente.trim();

    if (editar.peso !== "" && editar.peso !== null && editar.peso !== undefined) {
      const pesoNum = Number(editar.peso);
      if (!Number.isFinite(pesoNum) || pesoNum <= 0) {
        setMsgErr("El peso debe ser un número estrictamente > 0.");
        return;
      }
      payload.peso = pesoNum;
    }

    if (editar.destino?.trim()) payload.destino = editar.destino.trim();
    if (editar.centroActual?.trim()) payload.centroActual = editar.centroActual.trim();

    if (editar.estado?.trim()) {
      const est = normEstado(editar.estado);
      if (!ESTADOS_VALIDOS.includes(est)) {
        setMsgErr("Estado inválido. Use: PENDIENTE, EN_TRANSITO, ENTREGADO.");
        return;
      }
      payload.estado = est;
    }

    try {
      const res = await actualizarPaquete(seleccionId, payload);
      setMsgOk("Paquete actualizado.");
      await recargarLista();
      await seleccionar(res?.id || seleccionId);
    } catch (e) {
      setMsgErr(e.message || "No se pudo actualizar");
    }
  };

  const borrar = async () => {
    if (!seleccionId) return;
    limpiarMensajes();

    const est = normEstado(detalle?.estado ?? seleccionado?.estado);
    if (est === "EN_TRANSITO" || est === "ENTREGADO") {
      setMsgErr("No se puede eliminar: el paquete está EN_TRANSITO o ENTREGADO.");
      return;
    }

    if (!confirm(`¿Eliminar el paquete ${seleccionId}?`)) return;

    try {
      await eliminarPaquete(seleccionId);
      setMsgOk("Paquete eliminado.");
      setSeleccionId("");
      setDetalle(null);
      await recargarLista();
    } catch (e) {
      setMsgErr(e.message || "No se pudo eliminar");
    }
  };

  const colorEstado = (estado) => {
    const e = normEstado(estado);
    if (e === "PENDIENTE") return "#111";
    if (e === "EN_TRANSITO") return "#8a5a00";
    if (e === "ENTREGADO") return "#0b5f2a";
    return "#444";
  };

  return (
    <section style={{ display: "grid", gap: 16 }}>
      <h2 style={{ margin: 0 }}>Gestión de Paquetes</h2>

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
        {/* LISTA */}
        <div style={{ padding: 12, border: "1px solid #ddd", borderRadius: 10 }}>
          <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center", gap: 10 }}>
            <strong>Paquetes</strong>
            <button onClick={recargarLista} style={{ padding: "6px 10px" }}>
              Recargar
            </button>
          </div>

          {loadingLista && <p style={{ marginTop: 10 }}>Cargando...</p>}
          {errorLista && <p style={{ marginTop: 10, color: "crimson" }}>{errorLista}</p>}

          {!loadingLista && !errorLista && paquetes.length === 0 && (
            <p style={{ marginTop: 10 }}>No hay paquetes cargados.</p>
          )}

          {!loadingLista && paquetes.length > 0 && (
            <div style={{ marginTop: 10, display: "grid", gap: 8 }}>
              {paquetes.map((p) => (
                <button
                  key={p.id}
                  onClick={() => seleccionar(p.id)}
                  style={{
                    textAlign: "left",
                    padding: 10,
                    borderRadius: 10,
                    border: seleccionId === p.id ? "2px solid #333" : "1px solid #ddd",
                    background: seleccionId === p.id ? "#111" : "white",
                    color: seleccionId === p.id ? "white" : "#111",
                    cursor: "pointer",
                  }}
                >
                  <div style={{ display: "flex", justifyContent: "space-between", gap: 10 }}>
                    <div>
                      <strong>{p.id}</strong> — {p.cliente}
                    </div>
                    <span
                      style={{
                        fontSize: 12,
                        fontWeight: 700,
                        color: seleccionId === p.id ? "white" : colorEstado(p.estado),
                      }}
                    >
                      {normEstado(p.estado) || "SIN_ESTADO"}
                    </span>
                  </div>
                  <div style={{ fontSize: 13, opacity: 0.8 }}>
                    Origen: {p.centroActual} · Destino: {p.destino} · Peso: {p.peso}
                  </div>
                </button>
              ))}
            </div>
          )}
        </div>

        {/* DETALLE + EDICIÓN */}
        <div style={{ padding: 12, border: "1px solid #ddd", borderRadius: 10 }}>
          <strong>Detalle</strong>

          {!seleccionId && <p style={{ marginTop: 10 }}>Seleccione un paquete.</p>}

          {seleccionId && loadingDetalle && <p style={{ marginTop: 10 }}>Cargando detalle...</p>}
          {seleccionId && errorDetalle && <p style={{ marginTop: 10, color: "crimson" }}>{errorDetalle}</p>}

          {seleccionId && !loadingDetalle && detalle && (
            <div style={{ marginTop: 10, display: "grid", gap: 10 }}>
              {/* Info actual */}
              <div style={{ display: "grid", gap: 6 }}>
                <div><strong>ID:</strong> {detalle.id}</div>
                <div><strong>Cliente:</strong> {detalle.cliente}</div>
                <div><strong>Peso:</strong> {detalle.peso}</div>
                <div><strong>Centro actual:</strong> {detalle.centroActual}</div>
                <div><strong>Destino:</strong> {detalle.destino}</div>
                <div>
                  <strong>Estado:</strong>{" "}
                  <span style={{ fontWeight: "bold", color: colorEstado(detalle.estado) }}>
                    {normEstado(detalle.estado)}
                  </span>
                </div>
              </div>

              <hr style={{ margin: "6px 0" }} />

              {/* Form edición */}
              <div style={{ display: "grid", gap: 8 }}>
                <strong>Editar paquete</strong>

                <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr", gap: 10 }}>
                  <input
                    placeholder="Cliente"
                    value={editar.cliente}
                    onChange={(e) => setEditar({ ...editar, cliente: e.target.value })}
                  />
                  <input
                    placeholder="Peso (>0)"
                    type="number"
                    value={editar.peso}
                    onChange={(e) => setEditar({ ...editar, peso: e.target.value })}
                  />
                  <input
                    placeholder="Destino (ej: C002)"
                    value={editar.destino}
                    onChange={(e) => setEditar({ ...editar, destino: e.target.value })}
                  />
                  <input
                    placeholder="Centro actual (ej: C001)"
                    value={editar.centroActual}
                    onChange={(e) => setEditar({ ...editar, centroActual: e.target.value })}
                  />
                </div>

                <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr", gap: 10 }}>
                  <select
                    value={normEstado(editar.estado) || ""}
                    onChange={(e) => setEditar({ ...editar, estado: e.target.value })}
                  >
                    <option value="">(No cambiar estado)</option>
                    {ESTADOS_VALIDOS.map((e) => (
                      <option key={e} value={e}>
                        {e}
                      </option>
                    ))}
                  </select>

                  <button onClick={guardarCambios} style={{ padding: "6px 10px" }}>
                    Guardar cambios
                  </button>
                </div>

                <button
                  onClick={borrar}
                  style={{ padding: "6px 10px" }}
                  disabled={["EN_TRANSITO", "ENTREGADO"].includes(normEstado(detalle.estado))}
                  title={
                    ["EN_TRANSITO", "ENTREGADO"].includes(normEstado(detalle.estado))
                      ? "No permitido por regla de negocio"
                      : ""
                  }
                >
                  Eliminar paquete
                </button>

                <p style={{ margin: 0, fontSize: 12, opacity: 0.75 }}>
                  Reglas: peso &gt; 0, destino debe existir, estados válidos, no borrar EN_TRANSITO/ENTREGADO.
                </p>
              </div>
            </div>
          )}
        </div>
      </div>

      {/* CREAR */}
      <div style={{ padding: 12, border: "1px solid #ddd", borderRadius: 10 }}>
        <strong>Crear paquete</strong>

        <form onSubmit={crear} style={{ marginTop: 10, display: "grid", gap: 10 }}>
          <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr", gap: 10 }}>
            <input
              placeholder="ID (ej: P010)"
              value={nuevo.id}
              onChange={(e) => setNuevo({ ...nuevo, id: e.target.value })}
            />
            <input
              placeholder="Cliente"
              value={nuevo.cliente}
              onChange={(e) => setNuevo({ ...nuevo, cliente: e.target.value })}
            />
            <input
              placeholder="Peso (>0)"
              type="number"
              value={nuevo.peso}
              onChange={(e) => setNuevo({ ...nuevo, peso: e.target.value })}
            />
            <input
              placeholder="Destino (ej: C002)"
              value={nuevo.destino}
              onChange={(e) => setNuevo({ ...nuevo, destino: e.target.value })}
            />
            <input
              placeholder="Centro actual (ej: C001)"
              value={nuevo.centroActual}
              onChange={(e) => setNuevo({ ...nuevo, centroActual: e.target.value })}
            />
            <select
              value={normEstado(nuevo.estado)}
              onChange={(e) => setNuevo({ ...nuevo, estado: e.target.value })}
            >
              {ESTADOS_VALIDOS.map((e) => (
                <option key={e} value={e}>
                  {e}
                </option>
              ))}
            </select>
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
