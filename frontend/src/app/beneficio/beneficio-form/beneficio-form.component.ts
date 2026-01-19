import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, ActivatedRoute, RouterModule } from '@angular/router';
import { BeneficioService } from '../../services/beneficio.service';
import { Beneficio } from '../../models/beneficio';

@Component({
  selector: 'app-beneficio-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterModule],
  template: `
    <div class="card">
      <h2>{{ isEditMode ? 'Editar Benefício' : 'Novo Benefício' }}</h2>

      <div *ngIf="error" class="alert alert-error">
        {{ error }}
      </div>

      <form [formGroup]="form" (ngSubmit)="salvar()">
        <div class="form-group">
          <label for="nome">Nome *</label>
          <input 
            id="nome" 
            type="text" 
            formControlName="nome"
            [class.error]="form.get('nome')?.invalid && form.get('nome')?.touched">
          <div *ngIf="form.get('nome')?.invalid && form.get('nome')?.touched" class="error-message">
            Nome é obrigatório
          </div>
        </div>

        <div class="form-group">
          <label for="descricao">Descrição</label>
          <textarea 
            id="descricao" 
            formControlName="descricao"
            rows="3"></textarea>
        </div>

        <div class="form-group">
          <label for="valor">Valor *</label>
          <input 
            id="valor" 
            type="number" 
            step="0.01" 
            min="0"
            formControlName="valor"
            [class.error]="form.get('valor')?.invalid && form.get('valor')?.touched">
          <div *ngIf="form.get('valor')?.invalid && form.get('valor')?.touched" class="error-message">
            Valor deve ser maior ou igual a zero
          </div>
        </div>

        <div class="form-group">
          <label>
            <input type="checkbox" formControlName="ativo">
            Ativo
          </label>
        </div>

        <div style="display: flex; gap: 10px; margin-top: 20px;">
          <button type="submit" class="btn btn-primary" [disabled]="form.invalid || saving">
            {{ saving ? 'Salvando...' : 'Salvar' }}
          </button>
          <a routerLink="/beneficios" class="btn btn-secondary">Cancelar</a>
        </div>
      </form>
    </div>
  `
})
export class BeneficioFormComponent implements OnInit {
  form!: FormGroup;
  isEditMode = false;
  saving = false;
  error: string | null = null;

  constructor(
    private fb: FormBuilder,
    private service: BeneficioService,
    private router: Router,
    private route: ActivatedRoute
  ) {
    this.form = this.fb.group({
      nome: ['', Validators.required],
      descricao: [''],
      valor: [0, [Validators.required, Validators.min(0)]],
      ativo: [true]
    });
  }

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.isEditMode = true;
      this.carregarBeneficio(Number(id));
    }
  }

  carregarBeneficio(id: number): void {
    this.service.buscarPorId(id).subscribe({
      next: (beneficio) => {
        this.form.patchValue({
          nome: beneficio.nome,
          descricao: beneficio.descricao || '',
          valor: beneficio.valor,
          ativo: beneficio.ativo !== false
        });
      },
      error: (err) => {
        this.error = 'Erro ao carregar benefício: ' + (err.error?.message || err.message);
      }
    });
  }

  salvar(): void {
    if (this.form.valid) {
      this.saving = true;
      this.error = null;

      const beneficio = this.form.value;
      const id = this.route.snapshot.paramMap.get('id');

      const operacao = id 
        ? this.service.atualizar(Number(id), beneficio)
        : this.service.criar(beneficio);

      operacao.subscribe({
        next: () => {
          this.router.navigate(['/beneficios']);
        },
        error: (err) => {
          this.error = 'Erro ao salvar: ' + (err.error?.message || err.message);
          this.saving = false;
        }
      });
    }
  }
}
