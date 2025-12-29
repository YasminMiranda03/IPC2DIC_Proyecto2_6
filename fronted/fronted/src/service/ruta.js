const API_RUTAS = "http://localhost:8080/api/rutas";


export const obtenerRutas = async () => {
  try {
    const response = await fetch(API_RUTAS);
    if (!response.ok) {
      throw new Error("Error al obtener rutas");
    }
    const data = await response.json();
    return data;
  } catch (error) {
    console.error("Error al obtener rutas", error);
    throw error;
  }
};


export const obtenerRutaPorId = async (id) => {
  try {
    const response = await fetch(`${API_RUTAS}/${id}`);
    if (!response.ok) {
        throw new Error('Error al obtener la ruta')
    }
    const data = await response.json();
    return data;
  } catch (error) {
    console.error("Error al obtener ruta por id", error);
    throw error;
  }
};


export const crearRuta = async (ruta) => {
  try {
    const response = await fetch(API_RUTAS, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify(ruta),
    });
    if (!response.ok) {
        throw new Error('Error al crear la ruta')
    }
    const data = await response.json();
    return data;
  } catch (error) {
    console.error("Error al crearRuta", error);
    throw error;
  }
};


export const actualizarRuta = async (id, ruta) => {
  try {
    const response = await fetch(`${API_RUTAS}/${id}`, {
      method: "PUT",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify(ruta),
    });
    if (!response.ok) {
        throw new Error('Error al actualizar la ruta')
    }
    const data = await response.json();
    return data;
  } catch (error) {
    console.error("Error al actualizarRuta", error);
    throw error;
  }
};


export const eliminarRuta = async (id) => {
  try {
    const response = await fetch(`${API_RUTAS}/${id}`, {
      method: "DELETE",
    });
    if (!response.ok) {
        throw new Error('Error al eliminar la ruta')
    }
    const texto = await response.text();
    if (!texto) return true;
    try {
      return JSON.parse(texto);
    } catch {
      return texto; 
    }
  } catch (error) {
    console.error("Error al eliminarRuta", error);
    throw error;
  }
  
}
