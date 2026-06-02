import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { AuthService } from './auth.service';
import { Observable } from 'rxjs';
import { AssetRequest } from '../interfaces/AssetRequest';

@Injectable({
  providedIn: 'root',
})
export class AssetService {
  private http = inject(HttpClient);
  private authService = inject(AuthService);
  private apiUrl = 'http://localhost:8080/api/v1/assets';

  /**
   * Helper para adjuntar de manera automática el Token JWT
   * almacenado en el LocalStorage a cada petición HTTP.
   */
  private getHeaders(): HttpHeaders {
    const token = this.authService.getToken();
    return new HttpHeaders().set('Authorization', `Bearer ${token}`);
  }

  /**
   * Consulta avanzada de activos tecnológicos utilizando filtros simultáneos y paginación.
   */
  findAssets(filtros: any, page: number, size: number): Observable<any> {
    let params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());

    if (filtros.serialNumber) params = params.set('serialNumber', filtros.serialNumber);
    if (filtros.brandModel) params = params.set('brandModel', filtros.brandModel);
    if (filtros.idCategory) params = params.set('idCategory', filtros.idCategory.toString());
    if (filtros.status) params = params.set('status', filtros.status);
    if (filtros.costMin) params = params.set('costMin', filtros.costMin.toString());
    if (filtros.costMax) params = params.set('costMax', filtros.costMax.toString());

    return this.http.get<any>(this.apiUrl, { params, headers: this.getHeaders() });
  }

  /**
   * Registra un nuevo activo tecnológico en el inventario (Solo ADMIN).
   */
  registerAsset(asset: AssetRequest): Observable<any> {
    return this.http.post<any>(this.apiUrl, asset, { headers: this.getHeaders() });
  }

  /**
   * Actualiza todas las propiedades de un activo existente (Solo ADMIN).
   */
  updateAsset(idTechnical: string, asset: AssetRequest): Observable<any> {
    return this.http.put<any>(`${this.apiUrl}/${idTechnical}`, asset, { headers: this.getHeaders() });
  }

  /**
   * Actualización parcial para mutar únicamente el estado del activo (Gatilla validación de Baja).
   */
  updateAssetStatus(idTechnical: string, status: string): Observable<any> {
    let params = new HttpParams().set('status', status);
    return this.http.patch<any>(`${this.apiUrl}/${idTechnical}/status`, null, { params, headers: this.getHeaders() });
  }

  /**
   * Consume el endpoint extractor del ZIP empaquetado 100% en memoria (Excel + TXT Auditoría).
   */
  exportZipReport(filtros: any): Observable<any> {
    let params = new HttpParams();
    if (filtros.serialNumber) params = params.set('serialNumber', filtros.serialNumber);
    if (filtros.brandModel) params = params.set('brandModel', filtros.brandModel);
    if (filtros.idCategory) params = params.set('idCategory', filtros.idCategory.toString());
    if (filtros.status) params = params.set('status', filtros.status);
    if (filtros.costMin) params = params.set('costMin', filtros.costMin.toString());
    if (filtros.costMax) params = params.set('costMax', filtros.costMax.toString());

    return this.http.get<any>(`${this.apiUrl}/export`, { params, headers: this.getHeaders() });
  }
}
