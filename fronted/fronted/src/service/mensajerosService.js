const API_URL = "http://localhost:8080/api/mensajeros";

async function handleJson(res) {
  const text = await res.text(); 
  let data = null;
  try {
    data = text ? JSON.parse(text) : null;
  } catch {
    data = text; 
  }

  if (!res.ok) {
    const msg =
      (typeof data === "string" && data) ||
      (data?.message) ||
      `Error HTTP ${res.status}`;
    throw new Error(msg);
  }
  return data;
}

export const obtenerMensajeros = async () => {
  const res = await fetch(API_URL);
  return handleJson(res);
};

export const obtenerMensajeroPorId = async (id) => {
  const res = await fetch(`${API_URL}/${encodeURIComponent(id)}`);
  return handleJson(res);
};

export const crearMensajero = async (mensajero) => {
  const res = await fetch(API_URL, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(mensajero),
  });
  return handleJson(res);
};

export const cambiarEstadoMensajero = async (id, nuevoEstado) => {
  const res = await fetch(
    `${API_URL}/${encodeURIComponent(id)}/estado?nuevoEstado=${encodeURIComponent(nuevoEstado)}`,
    { method: "PUT" }
  );
  return handleJson(res);
};

export const reasignarCentroMensajero = async (id, nuevoCentroId) => {
  const res = await fetch(
    `${API_URL}/${encodeURIComponent(id)}/centro?nuevoCentroId=${encodeURIComponent(nuevoCentroId)}`,
    { method: "PUT" }
  );
  return handleJson(res);
};

export const asignarEnvioDirecto = async (paqueteId, mensajeroId) => {
  const res = await fetch(`http://localhost:8080/api/envios/asignar`, {
    method: "PUT",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ paqueteId, mensajeroId }),
  });
  return handleJson(res);
};