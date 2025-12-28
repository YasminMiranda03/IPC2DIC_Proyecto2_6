const API_URL = "http://localhost:8080/api/mensajeros";

export async function obtenerMensajeros() {
  const response = await fetch("http://localhost:8080/api/mensajeros");
  return await response.json(); 
}


export async function crearMensajero(mensajero) {
  const response = await fetch("http://localhost:8080/api/mensajeros", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(mensajero)
  });

  return await response.json();
}


export const cambiarEstadoMensajero = async (id, nuevoEstado) => {
  const res = await fetch(
    `${API_URL}/${id}/estado?nuevoEstado=${nuevoEstado}`,
    { method: "PUT" }
  );
  return res.json();
};

export const reasignarCentro = async (id, nuevoCentroId) => {
  const res = await fetch(
    `${API_URL}/${id}/centro?nuevoCentroId=${nuevoCentroId}`,
    { method: "PUT" }
  );
  return res.json();
};


