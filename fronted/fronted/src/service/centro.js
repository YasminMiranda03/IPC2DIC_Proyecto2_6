const API_CENTROS = "http://localhost:8080/api/centros";




export const obtenerCentros = async () => {
  try {
    const response = await fetch(API_CENTROS);
    if (!response.ok) {
      throw new Error("Error al obtener centros");
    }
    const data = await response.json();
    return data;
  } catch (error) {
    console.error("Error al obtener centros", error);
    throw error;
  }
}

export const obtenerCentroPorId = async(id) => {
  try {
    const response = await fetch(`${API_CENTROS}/${id}`);
    if (!response.ok) {
      if (response.status === 404) {
        throw new Error(`Centro no encontrado: ${id}`);
      }
    }
    const data = await response.json();
    return data;
  } catch (error) {
    console.error("Error al obtener centro por id", error);
    throw error;
  }
}

export const obtenerPaquetesDeCentro = async (id) => {
  try {
    const response = await fetch(`${API_CENTROS}/${id}/paquetes`);
    if (!response.ok) {
      if (response.status === 404) {
        throw new Error(`Centro no encontrado: ${id}`);
      }
    }
    const data = await response.json();
    return data;
  } catch (error) {
    console.error("Error al obtener paquetes del centro", error);
    throw error;
  }
}


export const obtenerMensajerosDeCentro = async (id) => {
  try {
    const response = await fetch(`${API_CENTROS}/${id}/mensajeros`);
    if (!response.ok) {
      if (response.status === 404) {
        throw new Error(`Centro no encontrado: ${id}`);
      }
    }
    const data = await response.json();
    return data;
  } catch (error) {
    console.error("Error al obtener mensajeros del centro", error);
    throw error;
  }
}