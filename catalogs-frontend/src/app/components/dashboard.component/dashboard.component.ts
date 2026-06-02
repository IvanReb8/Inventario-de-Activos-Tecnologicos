import { Component, inject, OnInit, signal } from '@angular/core';
import { AuthService } from '../../services/auth.service';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AssetRequest } from '../../interfaces/AssetRequest';
import { AssetService } from '../../services/asset.service';
import { CategoryService } from '../../services/category.service';
import { CategoryResponse } from '../../interfaces/CategoryResponse';

/**
 * Componente de dashboard del frontend.
 *
 * Controla la vista principal de productos, incluyendo búsqueda filtrada,
 * paginación, exportación a Excel y los formularios de creación/edición.
 */
@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './dashboard.component.html'
})
export class DashboardComponent implements OnInit {
  // --- INYECCIÓN MODERNIZADA DE SERVICIOS (Angular 21) ---
  private assetService = inject(AssetService);
  private categoryService = inject(CategoryService);
  authService = inject(AuthService);

  // --- ESTADOS REACTIVOS MEDIANTE SIGNALS ---
  assets = signal<any[]>([]);
  categories = signal<CategoryResponse[]>([]);
  totalElements = signal<number>(0);
  totalPages = signal<number>(0);
  currentPage = signal<number>(0);
  pageSize = 5;

  // Control del comportamiento del Formulario Modal
  isEditMode = signal<boolean>(false);

  // --- MODELOS DE DATOS VINCULADOS A LA INTERFAZ (Vistas de Filtros y Formulario) ---
  filtros = {
    serialNumber: '',
    brandModel: '',
    idCategory: '' as string | number,
    status: '',
    costMin: null as number | null,
    costMax: null as number | null,
    sortBy: 'entryDate',
    sortDir: 'desc'
  };

  assetForm: AssetRequest & { idTechnical: string | null } = {
    idTechnical: null,
    serialNumber: '',
    brandModel: '',
    status: 'AVAILABLE', // Estado por defecto para altas
    acquisitionCost: null as any,
    idCategory: '' as any
  };

  ngOnInit() {
    this.loadCategories();
    this.loadAssets();
  }

  /**
   * Carga inicial de las categorías para alimentar los selectores desplegables de la UI
   */
  loadCategories() {
    this.categoryService.listAllCategories().subscribe({
      next: (res) => this.categories.set(res),
      error: () => alert('Error al inicializar el catálogo de categorías.')
    });
  }

  /**
   * Consulta paginada y filtrada de activos tecnológicos
   */
  loadAssets(page: number = 0) {
    // Validar rango de costos antes de realizar la consulta
    if (!this.isCostRangeValid()) {
      alert('Rango de costo inválido: el costo mínimo debe ser menor o igual al costo máximo.');
      return;
    }

    // Normalizar valores numéricos (asegurar que llegan como números o null)
    this.filtros.costMin != null ? Number(this.filtros.costMin) : null;
    this.filtros.costMax != null ? Number(this.filtros.costMax) : null;

    this.currentPage.set(page);
    this.assetService.findAssets(this.filtros, page, this.pageSize).subscribe({
      next: (res) => {
        this.assets.set(res.content);
        this.totalElements.set(res.totalElements);
        this.totalPages.set(res.totalPages);
      },
      error: (err) => {
        const errorMsg = err.error?.message || 'Error al conectar con el servidor de inventarios.';
        alert(errorMsg);
      }
    });
  }

  /**
   * Comprueba que el rango de costos corresponda a un intervalo válido.
   * Devuelve true cuando ambos valores son nulos/indefinidos o cuando min <= max.
   */
  isCostRangeValid(): boolean {
    const min = this.filtros.costMin;
    const max = this.filtros.costMax;

    if ((min === null || min === undefined) || (max === null || max === undefined)) {
      // Si uno de los valores no está definido, no hay restricción inválida
      return true;
    }

    const minNum = Number(min);
    const maxNum = Number(max);
    if (Number.isNaN(minNum) || Number.isNaN(maxNum)) return false;
    return minNum <= maxNum;
  }

  clearFilters() {
    this.filtros = { serialNumber: '', brandModel: '', idCategory: '', status: '', costMin: null, costMax: null, sortBy: 'entryDate', sortDir: 'desc' };
    this.loadAssets(0);
  }

  // --- MANEJO LOGÍSTICO DEL MODAL (ALTAS Y CAMBIOS) ---

  openCreateModal() {
    this.isEditMode.set(false);
    this.assetForm = {
      idTechnical: null,
      serialNumber: '',
      brandModel: '',
      status: 'AVAILABLE',
      acquisitionCost: null as any,
      idCategory: '' as any
    };
  }

  openEditModal(asset: any) {
    this.isEditMode.set(true);
    // Clonación limpia para mitigar mutaciones en caliente sobre la tabla
    this.assetForm = {
      idTechnical: asset.idTechnical,
      serialNumber: asset.serialNumber,
      brandModel: asset.brandModel,
      status: asset.status,
      acquisitionCost: asset.acquisitionCost,
      idCategory: asset.category.idCategory
    };
  }

  saveAsset() {
    // Convertimos explícitamente el id de categoría a número por si el binding de HTML lo pasa como string
    this.assetForm.idCategory = Number(this.assetForm.idCategory);

    if (this.isEditMode()) {
      // Flujo de Actualización (PUT) con captura homogénea de excepciones (ej. Bloqueo de Estado Baja)
      this.assetService.updateAsset(this.assetForm.idTechnical!, this.assetForm).subscribe({
        next: () => {
          alert('Activo modificado exitosamente en el inventario.');
          this.closeModalProgrammatically();
          this.loadAssets(this.currentPage());
        },
        error: (err) => alert('Restricción de Negocio: ' + (err.error?.message || 'Fallo al actualizar.'))
      });
    } else {
      // Flujo de Alta (POST) con validaciones (ej. Serie Única)
      this.assetService.registerAsset(this.assetForm).subscribe({
        next: () => {
          alert('Nuevo activo tecnológico registrado correctamente.');
          this.closeModalProgrammatically();
          this.loadAssets(0);
        },
        error: (err) => alert('Error de Validación: ' + (err.error?.message || 'Fallo al registrar.'))
      });
    }
  }

  private closeModalProgrammatically() {
    const closeBtn = document.getElementById('btnCerrarModal');
    if (closeBtn) closeBtn.click();
  }

  // --- 📦 REQUERIMIENTO DE ALTA COMPLEJIDAD: PROCESAMIENTO BINARIO ZIP EN MEMORIA ---
  downloadZipReport() {
    this.assetService.exportZipReport(this.filtros).subscribe({
      next: (response) => {
        const base64Data = response.fileBase64;
        const contentType = 'application/zip'; // Tipo MIME exacto para archivos comprimidos .zip

        // Algoritmo nativo de decodificación de datos a nivel de cliente
        const byteCharacters = atob(base64Data);
        const byteNumbers = new Array(byteCharacters.length);
        for (let i = 0; i < byteCharacters.length; i++) {
          byteNumbers[i] = byteCharacters.charCodeAt(i);
        }
        const byteArray = new Uint8Array(byteNumbers);
        const blob = new Blob([byteArray], { type: contentType });

        // Disparador sintético del evento de descarga en el navegador del sínodo
        const url = window.URL.createObjectURL(blob);
        const link = document.createElement('a');
        link.href = url;
        link.download = response.fileName || 'inventory_report.zip';
        link.click();
        window.URL.revokeObjectURL(url);
      },
      error: () => alert('Error crítico al empaquetar el ZIP en la memoria del servidor.')
    });
  }
}
