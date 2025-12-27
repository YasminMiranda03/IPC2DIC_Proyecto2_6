import { NavLink } from "react-router-dom";

// Menú de navegación principal (LogiTrack)
export default function NavBar() {
  return (
    <header className="navbar">
      <nav className="navbar-inner">
        <span className="brand">LogiTrack</span>

        <ul className="nav-links">
          <li>
            <NavLink
              to="/"
              className={({ isActive }) => (isActive ? "active" : undefined)}
              end
            >
              Inicio
            </NavLink>
          </li>

          <li>
            <NavLink
              to="/importar"
              className={({ isActive }) => (isActive ? "active" : undefined)}
            >
              Importar XML
            </NavLink>
          </li>

          <li>
            <NavLink
              to="/centros"
              className={({ isActive }) => (isActive ? "active" : undefined)}
            >
              Centros
            </NavLink>
          </li>

          <li>
            <NavLink
              to="/rutas"
              className={({ isActive }) => (isActive ? "active" : undefined)}
            >
              Rutas
            </NavLink>
          </li>

          <li>
            <NavLink
              to="/mensajeros"
              className={({ isActive }) => (isActive ? "active" : undefined)}
            >
              Mensajeros
            </NavLink>
          </li>

          <li>
            <NavLink
              to="/paquetes"
              className={({ isActive }) => (isActive ? "active" : undefined)}
            >
              Paquetes
            </NavLink>
          </li>

          <li>
            <NavLink
              to="/solicitudes"
              className={({ isActive }) => (isActive ? "active" : undefined)}
            >
              Solicitudes
            </NavLink>
          </li>


          <li>
            <NavLink
              to="/exportar"
              className={({ isActive }) => (isActive ? "active" : undefined)}
            >
              Exportar XML
            </NavLink>
          </li>
        </ul>
      </nav>
    </header>
  );
}