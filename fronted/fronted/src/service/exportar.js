const API_URL = "http://localhost:8080/api/exportar";

export const exportarXml = async () => {
  const res = await fetch(API_URL, {
    method: "GET",
    headers: {
      Accept: "application/xml",
    },
  });

  if (!res.ok) {
    const text = await res.text();
    throw new Error(text || `Error HTTP ${res.status}`);
  }

  return res.text(); 
};
