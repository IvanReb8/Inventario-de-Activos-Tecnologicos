CREATE TABLE categories (
    id_category BIGINT AUTO_INCREMENT PRIMARY KEY,
    category_name VARCHAR(100) NOT NULL,
    prefix_code VARCHAR(3) NOT NULL UNIQUE
);

CREATE TABLE technology_assets (
    id_technical VARCHAR(36) PRIMARY KEY, -- UUID
    inventory_folio VARCHAR(50) UNIQUE NOT NULL,
    serial_number VARCHAR(100) UNIQUE NOT NULL,
    brand_model VARCHAR(255) NOT NULL,
    status VARCHAR(30) NOT NULL, -- AVAILABLE, ASSIGNED, UNDER_MAINTENANCE, DISPOSED
    acquisition_cost DECIMAL(12,2) NOT NULL,
    entry_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    id_category BIGINT NOT NULL,
    FOREIGN KEY (id_category) REFERENCES categories(id_category)
);

-- ===================================================================
-- 🧹 LIMPIEZA DE TABLAS PREVIO A INSERCIÓN (Evita conflictos de duplicados)
-- ===================================================================
SET FOREIGN_KEY_CHECKS = 0;
TRUNCATE TABLE technology_assets;
TRUNCATE TABLE categories;
SET FOREIGN_KEY_CHECKS = 1;

-- ===================================================================
-- 📁 1. INSERCIÓN DE CATEGORÍAS BASE
-- ===================================================================
INSERT INTO categories (id_category, category_name, prefix_code) VALUES 
(1, 'Laptop', 'LAP'),
(2, 'Monitor', 'MON'),
(3, 'Celular', 'CEL');

-- ===================================================================
-- 📦 2. INSERCIÓN DE LOS 5 ACTIVOS TECNOLÓGICOS PARA PRUEBAS DE FILTROS
-- ===================================================================
INSERT INTO technology_assets 
(id_technical, inventory_folio, serial_number, brand_model, status, acquisition_cost, entry_date, id_category) 
VALUES 
-- Activo 1: Laptop de gama alta (Disponible y costo máximo)
('a1b2c3d4-e5f6-7a8b-9c0d-1e2f3a4b5c6d', 'LAP-2026-001', 'SN-LAP-2026A', 'MacBook Pro M3 Max', 'AVAILABLE', 3500.00, CURRENT_TIMESTAMP, 1),

-- Activo 2: Monitor corporativo (Asignado y costo medio)
('b2c3d4e5-f67a-8b9c-0d1e-2f3a4b5c6d7e', 'MON-2026-001', 'SN-MON-9875X', 'Dell UltraSharp 27', 'ASSIGNED', 450.00, CURRENT_TIMESTAMP, 2),

-- Activo 3: Celular en reparación (En mantenimiento y filtro de texto parcial)
('c3d4e5f6-7a8b-9c0d-1e2f-3a4b5c6d7e8f', 'CEL-2026-001', 'SN-CEL-5544M', 'Samsung Galaxy S24 Ultra', 'UNDER_MAINTENANCE', 1299.99, CURRENT_TIMESTAMP, 3),

-- Activo 4: Laptop económica (Asignado y rango intermedio)
('d4e5f67a-8b9c-0d1e-2f3a-4b5c6d7e8f9a', 'LAP-2026-002', 'SN-LAP-7766B', 'Lenovo ThinkPad L14', 'ASSIGNED', 850.00, CURRENT_TIMESTAMP, 1),

-- Activo 5: Monitor obsoleto (Para validar bloqueo estricto de Baja/Disposed)
('e5f67a8b-9c0d-1e2f-3a4b-5c6d7e8f9a0b', 'MON-2026-002', 'SN-MON-0011Z', 'HP ProDisplay P22', 'DISPOSED', 120.00, CURRENT_TIMESTAMP, 2);
