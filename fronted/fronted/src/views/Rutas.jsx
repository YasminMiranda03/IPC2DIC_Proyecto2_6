import { useEffect, useState } from "react" 
import { obtenerRutas, obtenerRutaPorId, crearRuta, actualizarRuta, eliminarRuta} from "../service/ruta" 

export default function Rutas() {
  
  const [rutas, setRutas] = useState([]) 
  const [loadingRutas, setLoadingRutas] = useState(true) 
  const [errorRutas, setErrorRutas] = useState("")
  const [rutaSeleccionadaId, setRutaSeleccionadaId] = useState(null)
  const [detalle, setDetalle] = useState(null)
  const [loadingDetalle, setLoadingDetalle] = useState(false)
  const [errorDetalle, setErrorDetalle] = useState("")
  const [modoEdicion, setModoEdicion] = useState(false)
  const [saving, setSaving] = useState(false)
  const [formError, setFormError] = useState("")
  const [formSuccess, setFormSuccess] = useState("")
  const [form, setForm] = useState({
    id: "",
    origen: "",
    destino: "",
    distancia: "",
  }) 

  useEffect(() => {
    cargarRutas() 
  }, []) 

  const cargarRutas = async () => {
    try {
      setLoadingRutas(true) 
      setErrorRutas("") 
      const data = await obtenerRutas() 
      setRutas(Array.isArray(data) ? data : []) 
    } catch (e) {
      setErrorRutas(e.message || "Error al cargar rutas") 
    } finally {
      setLoadingRutas(false) 
    }
  } 

  const seleccionarRuta = async (id) => {
    setRutaSeleccionadaId(id) 
    setDetalle(null) 
    setErrorDetalle("") 

    try {
      setLoadingDetalle(true) 
      const info = await obtenerRutaPorId(id) 
      setDetalle(info) 
    } catch (e) {
      setErrorDetalle(e.message || "Error al cargar detalle") 
    } finally {
      setLoadingDetalle(false) 
    }
  } 

  const limpiarFormulario = () => {
    setForm({ id: "", origen: "", destino: "", distancia: "" }) 
    setModoEdicion(false) 
    setFormError("") 
    setFormSuccess("") 
  } 

  const editarRuta = (ruta) => {
    setModoEdicion(true) 
    setFormError("") 
    setFormSuccess("") 
    setRutaSeleccionadaId(ruta.id) 
    setDetalle(ruta)  
    setForm({
      id: ruta.id || "",
      origen: ruta.origen || "",
      destino: ruta.destino || "",
      distancia: ruta.distancia ?? "",
    }) 
  } 

  const onChange = (e) => {
    setForm((prev) => ({ ...prev, [e.target.name]: e.target.value })) 
  } 

  const validar = () => {
    if (!form.id.trim()) return "El ID es obligatorio" 
    if (!form.origen.trim()) return "El origen es obligatorio" 
    if (!form.destino.trim()) return "El destino es obligatorio" 
    if (!String(form.distancia).trim()) return "La distancia es obligatoria" 

    const d = Number(form.distancia) 
    if (Number.isNaN(d) || d <= 0) return "La distancia debe ser un número > 0" 

    return "" 
  } 

  const handleSubmit = async (e) => {
    e.preventDefault() 
    setFormError("") 
    setFormSuccess("") 

    const msg = validar() 
    if (msg) {
      setFormError(msg) 
      return 
    }

    const payload = {
      id: form.id.trim(),
      origen: form.origen.trim(),
      destino: form.destino.trim(),
      distancia: Number(form.distancia),
    } 

    try {
      setSaving(true) 

      if (!modoEdicion) {
        await crearRuta(payload) 
        setFormSuccess("Ruta creada correctamente.") 
      } else {
        await actualizarRuta(payload.id, payload) 
        setFormSuccess("Ruta actualizada correctamente.") 
      }

      await cargarRutas() 
  
      if (payload.id) await seleccionarRuta(payload.id) 

      if (!modoEdicion) limpiarFormulario() 
    } catch (err) {
      setFormError(err.message || "Error al guardar ruta") 
    } finally {
      setSaving(false) 
    }
  } 

  const handleEliminar = async (id) => {
    const ok = window.confirm(`¿Eliminar ruta ${id}?`) 
    if (!ok) return 

    try {
      setErrorRutas("") 
      await eliminarRuta(id) 

    
      if (rutaSeleccionadaId === id) {
        setRutaSeleccionadaId(null) 
        setDetalle(null) 
      }

      await cargarRutas() 
    } catch (e) {
      setErrorRutas(e.message || "Error al eliminar ruta") 
    }
  } 

  return (
    <section style={{ display: "grid", gap: 16 }}>
      <h2 style={{ margin: 0 }}>Gestión de Rutas</h2>

  
      <div style={{ display: "grid", gap: 10, padding: 12, border: "1px solid #ddd", borderRadius: 8 }}>
        <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center", gap: 12 }}>
          <strong>Rutas</strong>
        </div>

        {loadingRutas && <p>Cargando rutas...</p>}
        {errorRutas && <p style={{ color: "crimson" }}>{errorRutas}</p>}

        {!loadingRutas && !errorRutas && rutas.length === 0 && <p>No hay rutas cargadas.</p>}

        {!loadingRutas && rutas.length > 0 && (
          <div style={{ display: "grid", gap: 8 }}>
            {rutas.map((r) => (
              <div
                key={r.id}
                style={{
                  display: "grid",
                  gap: 6,
                  padding: 10,
                  borderRadius: 8,
                  border: rutaSeleccionadaId === r.id ? "2px solid #111edbff" : "1px solid #77ee15ff",
                  background: rutaSeleccionadaId === r.id ? "#dd1010ff" : "white",
                }}
              >
                <button
                  onClick={() => seleccionarRuta(r.id)}
                  style={{
                    textAlign: "left",
                    border: "none",
                    background: "transparent",
                    cursor: "pointer",
                    padding: 0,
                    color: "#111", 
                }}
                >
                  <div><strong>{r.id}</strong> origen {r.origen} destino {r.destino}</div>
                  <div style={{ fontSize: 13, opacity: 0.8 }}>Distancia: {r.distancia}</div>
                </button>

                <div style={{ display: "flex", gap: 8, flexWrap: "wrap" }}>
                  <button onClick={() => editarRuta(r)} style={{ padding: "6px 10px" }}>Actualizar</button>
                  <button onClick={() => handleEliminar(r.id)} style={{ padding: "6px 10px" }}>Eliminar</button>
                </div>
              </div>
            ))}
          </div>
        )}
      </div>

    
      <div style={{ padding: 12, border: "1px solid #d41919ff", borderRadius: 8 }}>
        <strong>Detalle de ruta</strong>

        {!rutaSeleccionadaId && <p style={{ marginTop: 8 }}>Seleccione una ruta para ver detalle.</p>}

        {rutaSeleccionadaId && loadingDetalle && <p style={{ marginTop: 8 }}>Cargando detalle...</p>}
        {rutaSeleccionadaId && errorDetalle && <p style={{ marginTop: 8, color: "crimson" }}>{errorDetalle}</p>}

        {rutaSeleccionadaId && !loadingDetalle && detalle && (
          <div style={{ marginTop: 10, display: "grid", gap: 6 }}>
            <div><strong>ID:</strong> {detalle.id}</div>
            <div><strong>Origen:</strong> {detalle.origen}</div>
            <div><strong>Destino:</strong> {detalle.destino}</div>
            <div><strong>Distancia:</strong> {detalle.distancia}</div>
          </div>
        )}
      </div>

      <div style={{ padding: 12, border: "1px solid #ddd", borderRadius: 8 }}>
        <strong>{modoEdicion ? "Actualizar ruta" : "Crear ruta"}</strong>

        <form onSubmit={handleSubmit} style={{ marginTop: 10, display: "grid", gap: 10 }}>
          <div style={{ display: "grid", gap: 6 }}>
            <label>ID</label>
            <input
              name="id"
              value={form.id}
              onChange={onChange}
      
              disabled={modoEdicion} 
            />
          </div>

          <div style={{ display: "grid", gap: 6 }}>
            <label>Origen (ID Centro)</label>
            <input name="origen" value={form.origen} onChange={onChange} />
          </div>

          <div style={{ display: "grid", gap: 6 }}>
            <label>Destino (ID Centro)</label>
            <input name="destino" value={form.destino} onChange={onChange} />
          </div>

          <div style={{ display: "grid", gap: 6 }}>
            <label>Distancia</label>
            <input name="distancia" value={form.distancia} onChange={onChange}  />
          </div>

          {formError && <p style={{ color: "crimson" }}>{formError}</p>}
          {formSuccess && <p style={{ color: "green" }}>{formSuccess}</p>}

          <div style={{ display: "flex", gap: 10, flexWrap: "wrap" }}>
            <button type="submit" disabled={saving} style={{ padding: "6px 10px" }}>
              {saving ? "Guardando..." : "Crear"}
            </button>

            <button type="button" onClick={limpiarFormulario} style={{ padding: "6px 10px" }}>
              Regresar
            </button>
          </div>
        </form>
      </div>
    </section>
  ) 
}