import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { BeneficioService } from '../../services/beneficio.service';
import { Beneficio } from '../../models/beneficio';

@Component({
  selector: 'app-beneficio-list',
  standalone: true,
  imports: [CommonModule, RouterModule],
  template: `
    <div class="card">
      <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px;">
        <h2>Lista de Benefícios</h2>
        <a routerLink="/beneficios/novo" class="btn btn-primary">+ Novo Benefício</a>
      </div>

      <div *ngIf="loading" class="loading">Carregando...</div>

      <div *ngIf="error" class="alert alert-error">
        {{ error }}
      </div>

      <div *ngIf="!loading && !error">
        <table class="table" *ngIf="beneficios.length > 0; else emptyState">
          <thead>
            <tr>
              <th>ID</th>
              <th>Nome</th>
              <th>Descrição</th>
              <th>Valor</th>
              <th>Status</th>
              <th>Ações</th>
            </tr>
          </thead>
          <tbody>
            <tr *ngFor="let beneficio of beneficios">
              <td>{{ beneficio.id }}</td>
              <td>{{ beneficio.nome }}</td>
              <td>{{ beneficio.descricao || '-' }}</td>
              <td>R$ {{ beneficio.valor | number:'1.2-2' }}</td>
              <td>
                <span [class]="beneficio.ativo ? 'badge-success' : 'badge-danger'">
                  {{ beneficio.ativo ? 'Ativo' : 'Inativo' }}
                </span>
              </td>
              <td>
                <a [routerLink]="['/beneficios/editar', beneficio.id]" class="btn btn-primary" style="padding: 5px 10px; font-size: 12px;">Editar</a>
                <button (click)="deletar(beneficio.id!)" class="btn btn-danger" style="padding: 5px 10px; font-size: 12px;">Excluir</button>
              </td>
            </tr>
          </tbody>
        </table>

        <ng-template #emptyState>
          <div class="empty-state">
            <div class="icon">📋</div>
            <p>Nenhum benefício cadastrado</p>
            <a routerLink="/beneficios/novo" class="btn btn-primary">Criar Primeiro Benefício</a>
          </div>
        </ng-template>
      </div>
    </div>
  `,
  styles: [`
    .badge-success {
      background: #27ae60;
      color: white;
      padding: 4px 8px;
      border-radius: 3px;
      font-size: 12px;
    }

    .badge-danger {
      background: #e74c3c;
      color: white;
      padding: 4px 8px;
      border-radius: 3px;
      font-size: 12px;
    }
  `]
})
export class BeneficioListComponent implements OnInit {
  beneficios: Beneficio[] = [];
  loading = false;
  error: string | null = null;

  constructor(private service: BeneficioService) { }

  ngOnInit(): void {
    this.carregarBeneficios();
  }

  carregarBeneficios(): void {
    this.loading = true;
    this.error = null;
    this.service.listar().subscribe({
      next: (data) => {
        this.beneficios = data;
        this.loading = false;
      },
      error: (err) => {
        this.error = 'Erro ao carregar benefícios: ' + (err.error?.message || err.message);
        this.loading = false;
      }
    });
  }

  deletar(id: number): void {
    if (confirm('Tem certeza que deseja excluir este benefício?')) {
      this.service.deletar(id).subscribe({
        next: () => {
          this.carregarBeneficios();
        },
        error: (err) => {
          alert('Erro ao excluir: ' + (err.error?.message || err.message));
        }
      });
    }
  }
}
