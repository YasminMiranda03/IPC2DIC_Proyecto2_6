import { useEffect, useMemo, useState } from "react";
import {
  obtenerSolicitudes,
  crearSolicitud,
  eliminarSolicitud,
  procesarMayorPrioridad,
  procesarNSolicitudes,
} from "../service/solicitudesService";

export default function Solicitudes() {
  const [solicitudes, setSolicitudes] = useState([]);
  const [loadingLista, setLoadingLista] = useState(true);
  const [errorLista, setErrorLista] = useState("");

  const [seleccionId, setSeleccionId] = useState("");
  const seleccionado = useMemo(
    () => solicitudes.find((s) => s?.id === seleccionId) || null,
    [solicitudes, seleccionId]
  );
  const [nuevo, setNuevo] = useState({
    id: "",
    tipo: "EnvioNormal",
    paquete: "",
    prioridad: "",
  });

  const [msgOk, setMsgOk] = useState("");
  const [msgErr, setMsgErr] = useState("");
  const [loadingProc, setLoadingProc] = useState(false);
  const [proc, setProc] = useState(null);

  const limpiarMensajes = () => {
    setMsgOk("");
    setMsgErr("");
  };

  const recargarLista = async () => {
    try {
      setLoadingLista(true);
      setErrorLista("");
      const data = await obtenerSolicitudes();
      setSolicitudes(Array.isArray(data) ? data : []);
    } catch (e) {
      setErrorLista(e.message || "Error al cargar solicitudes");
      setSolicitudes([]);
    } finally {
      setLoadingLista(false);
    }
  };

  useEffect(() => {
    recargarLista();
  }, []);

  const seleccionar = (id) => {
    limpiarMensajes();
    setSeleccionId(id);
  };


  const crear = async (e) => {
    e.preventDefault();
    limpiarMensajes();
    setProc(null);

    if (!nuevo.id.trim()) return setMsgErr("Complete el ID de la solicitud.");
    if (!nuevo.paquete.trim()) return setMsgErr("Complete el ID del paquete.");

    const pr = Number(nuevo.prioridad);
    if (!Number.isFinite(pr)) return setMsgErr("La prioridad debe ser un número.");

    try {
      await crearSolicitud({
        id: nuevo.id.trim(),
        tipo: nuevo.tipo?.trim() || "EnvioNormal",
        paqueteId: nuevo.paquete.trim(), 
        prioridad: pr,
      });

      setMsgOk("Solicitud creada.");
      setNuevo({ id: "", tipo: "EnvioNormal", paquete: "", prioridad: "" });
      await recargarLista();
    } catch (e) {
      setMsgErr(e.message || "Error creando solicitud");
    }
  };

  const eliminar = async (id) => {
    limpiarMensajes();
    setProc(null);

    if (!id) return;
    if (!confirm(`¿Eliminar solicitud ${id}?`)) return;

    try {
      await eliminarSolicitud(id);
      setMsgOk("Solicitud eliminada.");
      if (seleccionId === id) setSeleccionId("");
      await recargarLista();
    } catch (e) {
      setMsgErr(e.message || "No se pudo eliminar");
    }
  };

  const normalizarProc = (res) => {
    const exito = !!res?.exito;
    const motivo = res?.motivo ?? null;
    const solicitudesAtendidas = Array.isArray(res?.solicitudesAtendidas)
      ? res.solicitudesAtendidas
      : [];
    return { exito, motivo, solicitudesAtendidas };
  };

  const procesar1 = async () => {
    limpiarMensajes();
    setProc(null);

    try {
      setLoadingProc(true);
      const res = await procesarMayorPrioridad();
      const n = normalizarProc(res);
      setProc(n);

      if (n.exito) setMsgOk("Procesamiento exitoso.");
      else setMsgErr(n.motivo || "Procesamiento falló.");

      await recargarLista();
    } catch (e) {
      setMsgErr(e.message || "Error al procesar");
    } finally {
      setLoadingProc(false);
    }
  };

  const procesarN = async () => {
    limpiarMensajes();
    setProc(null);

    const nStr = prompt("¿Cuántas solicitudes desea procesar?");
    if (!nStr) return;

    const nVal = Number(nStr);
    if (!Number.isFinite(nVal) || nVal <= 0) {
      setMsgErr("n debe ser un número mayor a 0.");
      return;
    }

    try {
      setLoadingProc(true);
      const res = await procesarNSolicitudes(nVal);
      const n = normalizarProc(res);
      setProc(n);

      if (n.exito) setMsgOk(`Procesadas ${nVal} solicitudes.`);
      else setMsgErr(n.motivo || "Procesamiento falló.");

      await recargarLista();
    } catch (e) {
      setMsgErr(e.message || "Error al procesar N");
    } finally {
      setLoadingProc(false);
    }
  };

  return (
    <section style={{ display: "grid", gap: 16 }}>
      <h2>Gestión de Solicitudes (Cola de Prioridad)</h2>

      {(msgOk || msgErr) && (
        <div
          style={{
            padding: 10,
            borderRadius: 8,
            background: msgErr ? "#ffe9e9" : "#e9fff0",
            color: msgErr ? "#a10000" : "#0b5f2a",
          }}
        >
          {msgErr || msgOk}
        </div>
      )}

    
      <div style={{ border: "1px solid #ddd", padding: 12, borderRadius: 10 }}>
        <button onClick={procesar1} disabled={loadingProc}>
          Procesar mayor prioridad
        </button>
        <button onClick={procesarN} disabled={loadingProc}>
          Procesar N solicitudes
        </button>
        <button onClick={recargarLista}>Recargar</button>
      </div>

   
      <div style={{ border: "1px solid #ddd", padding: 12, borderRadius: 10 }}>
        <strong>Cola</strong>

        {loadingLista && <p>Cargando...</p>}
        {errorLista && <p style={{ color: "crimson" }}>{errorLista}</p>}

        {solicitudes.map((s) => (
          <button
            key={s.id}
            onClick={() => seleccionar(s.id)}
            style={{
              display: "block",
              marginTop: 6,
              width: "100%",
              textAlign: "left",
            }}
          >
            <strong>{s.id}</strong> — {s.tipo} | Prioridad {s.prioridad}
            <div>Paquete: {s.paqueteId}</div>
          </button>
        ))}
      </div>

    
      {seleccionado && (
        <div style={{ border: "1px solid #ddd", padding: 12, borderRadius: 10 }}>
          <strong>Detalle</strong>
          <div>ID: {seleccionado.id}</div>
          <div>Tipo: {seleccionado.tipo}</div>
          <div>Paquete: {seleccionado.paqueteId}</div>
          <div>Prioridad: {seleccionado.prioridad}</div>

          <button onClick={() => eliminar(seleccionado.id)}>Eliminar</button>
        </div>
      )}

    
      <form onSubmit={crear} style={{ border: "1px solid #ddd", padding: 12 }}>
        <strong>Crear solicitud</strong>

        <input
          value={nuevo.id}
          onChange={(e) => setNuevo({ ...nuevo, id: e.target.value })}
        />

        <select
          value={nuevo.tipo}
          onChange={(e) => setNuevo({ ...nuevo, tipo: e.target.value })}
        >
          <option value="EnvioNormal">EnvioNormal</option>
          <option value="EnvioExpress">EnvioExpress</option>
        </select>

        <input
          value={nuevo.paquete}
          onChange={(e) => setNuevo({ ...nuevo, paquete: e.target.value })}
        />

        <input
          type="number"
          value={nuevo.prioridad}
          onChange={(e) => setNuevo({ ...nuevo, prioridad: e.target.value })}
        />

        <button type="submit">Guardar</button>
      </form>
    </section>
  );
}
