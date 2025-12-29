const API_URL = "http://localhost:8080/api/solicitudes";

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
      data?.message ||
      `Error HTTP ${res.status}`;
    throw new Error(msg);
  }

  return data;
}

export const obtenerSolicitudes = async () => {
  const res = await fetch(API_URL);
  return handleJson(res);
};

export const crearSolicitud = async (solicitud) => {
  const res = await fetch(API_URL, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(solicitud),
  });
  return handleJson(res);
};

export const eliminarSolicitud = async (id) => {
  const res = await fetch(`${API_URL}/${encodeURIComponent(id)}`, {
    method: "DELETE",
  });
  return handleJson(res);
};

export const procesarMayorPrioridad = async () => {
  const res = await fetch(`${API_URL}/procesar`, { method: "POST" });
  return handleJson(res);
};

export const procesarNSolicitudes = async (n) => {
  const res = await fetch(`${API_URL}/procesar/${encodeURIComponent(n)}`, {
    method: "POST",
  });
  return handleJson(res);
};
