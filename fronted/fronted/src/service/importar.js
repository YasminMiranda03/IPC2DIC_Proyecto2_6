const API_IMPORTAR = "http://localhost:8080/api/importar";

export const importarXml = async (file) => {
  if (!file) throw new Error("Seleccione un archivo XML primero.");

  const formData = new FormData();
  formData.append("file", file);

  const res = await fetch(API_IMPORTAR, {
    method: "POST",
    body: formData,
  });

  const texto = await res.text();
  if (!res.ok) throw new Error(texto);

  return texto;
};