const API_URL = "http://localhost:8080/api/paquetes";

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

export const obtenerPaquetes = async () => {
  const res = await fetch(API_URL);
  return handleJson(res);
};

export const obtenerPaquetePorId = async (id) => {
  const res = await fetch(`${API_URL}/${encodeURIComponent(id)}`);
  return handleJson(res);
};

export const crearPaquete = async (paquete) => {
  const res = await fetch(API_URL, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(paquete),
  });
  return handleJson(res);
};

export const actualizarPaquete = async (id, paquete) => {
  const res = await fetch(`${API_URL}/${encodeURIComponent(id)}`, {
    method: "PUT",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(paquete),
  });
  return handleJson(res);
};

export const eliminarPaquete = async (id) => {
  const res = await fetch(`${API_URL}/${encodeURIComponent(id)}`, {
    method: "DELETE",
  });
  return handleJson(res);
};
