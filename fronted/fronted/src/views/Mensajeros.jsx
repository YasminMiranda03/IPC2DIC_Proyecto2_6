import "../styles/mensajeros.css";

import { useEffect, useState } from "react";
import {
  obtenerMensajeros,
  crearMensajero,
  cambiarEstadoMensajero,
  reasignarCentro
} from "../service/mensajerosService.js";

export default function Mensajeros() {
  const [mensajeros, setMensajeros] = useState([]);
  const [nuevo, setNuevo] = useState({
    id: "",
    nombre: "",
    capacidad: 0,
    centroId: ""
  });

  const cargarMensajeros = async () => {
    const data = await obtenerMensajeros();
    setMensajeros(data);
  };

  useEffect(() => {
    cargarMensajeros();
  }, []);

  const crear = async () => {
    await crearMensajero({
      ...nuevo,
      estado: "DISPONIBLE"
    });
    setNuevo({ id: "", nombre: "", capacidad: 0, centroId: "" });
    cargarMensajeros();
  };

  const estadoStyle = (estado) => {
  if (estado === "DISPONIBLE") {
    return { color: "green", fontWeight: "bold" };
  }
  if (estado === "EN_TRANSITO") {
    return { color: "orange", fontWeight: "bold" };
  }
  return {};
};

  return (
    <div className="mensajeros-container">
        <h2 className="mensajeros-title">Gestión de Mensajeros</h2>

        {/* CREAR */}
        <div className="card">
        <h3>Crear Mensajero</h3>

        <div className="form-row">
            <input placeholder="ID" value={nuevo.id}
            onChange={e => setNuevo({ ...nuevo, id: e.target.value })} />

            <input placeholder="Nombre" value={nuevo.nombre}
            onChange={e => setNuevo({ ...nuevo, nombre: e.target.value })} />

            <input placeholder="Capacidad" type="number" value={nuevo.capacidad}
            onChange={e => setNuevo({ ...nuevo, capacidad: e.target.value })} />

            <input placeholder="Centro ID" value={nuevo.centroId}
            onChange={e => setNuevo({ ...nuevo, centroId: e.target.value })} />
        </div>

        <br />
        <button className="btn btn-primary" onClick={crear}>
            Crear Mensajero
        </button>
        </div>

        {/* TABLA */}
        <div className="card">
        <h3>Listado</h3>

        <table className="table">
            <thead>
            <tr>
                <th>ID</th>
                <th>Nombre</th>
                <th>Centro</th>
                <th>Estado</th>
                <th>Acciones</th>
            </tr>
            </thead>

            <tbody>
            {mensajeros.map(m => (
                <tr key={m.id}>
                <td>{m.id}</td>
                <td>{m.nombre}</td>
                <td>{m.centroId}</td>
                <td className={
                    m.estado === "DISPONIBLE"
                    ? "estado-disponible"
                    : "estado-transito"
                }>
                    {m.estado}
                </td>

                <td className="actions">
                    <button
                    className="btn btn-warning"
                    disabled={m.estado === "EN_TRANSITO"}
                    onClick={() =>
                        cambiarEstadoMensajero(m.id, "EN_TRANSITO")
                        .then(cargarMensajeros)
                    }
                    >
                    En tránsito
                    </button>

                    <button
                    className="btn btn-secondary"
                    disabled={m.estado === "DISPONIBLE"}
                    onClick={() =>
                        cambiarEstadoMensajero(m.id, "DISPONIBLE")
                        .then(cargarMensajeros)
                    }
                    >
                    Disponible
                    </button>

                    <button
                    className="btn btn-danger"
                    disabled={m.estado === "EN_TRANSITO"}
                    onClick={() => {
                        const nuevoCentro = prompt("Nuevo centro ID:");
                        if (nuevoCentro)
                        reasignarCentro(m.id, nuevoCentro)
                            .then(cargarMensajeros);
                    }}
                    >
                    Cambiar centro
                    </button>
                </td>
                </tr>
            ))}
            </tbody>
        </table>
        </div>
    </div>
    );

}
