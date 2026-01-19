import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { BeneficioService } from '../services/beneficio.service';
import { Beneficio } from '../models/beneficio';
import { TransferenciaRequest } from '../models/transferencia-request';

@Component({
  selector: 'app-transferencia',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  template: `
    <div class="card">
      <h2>💸 Transferência entre Benefícios</h2>

      <div *ngIf="successMessage" class="alert alert-success">
        {{ successMessage }}
      </div>

      <div *ngIf="error" class="alert alert-error">
        {{ error }}
      </div>

      <form [formGroup]="form" (ngSubmit)="transferir()">
        <div class="form-group">
          <label for="fromId">Benefício de Origem *</label>
          <select 
            id="fromId" 
            formControlName="fromId"
            [class.error]="form.get('fromId')?.invalid && form.get('fromId')?.touched">
            <option value="">Selecione...</option>
            <option *ngFor="let b of beneficios" [value]="b.id">{{ b.nome }} (R$ {{ b.valor | number:'1.2-2' }})</option>
          </select>
          <div *ngIf="form.get('fromId')?.invalid && form.get('fromId')?.touched" class="error-message">
            Selecione o benefício de origem
          </div>
        </div>

        <div class="form-group">
          <label for="toId">Benefício de Destino *</label>
          <select 
            id="toId" 
            formControlName="toId"
            [class.error]="form.get('toId')?.invalid && form.get('toId')?.touched">
            <option value="">Selecione...</option>
            <option *ngFor="let b of beneficios" [value]="b.id">{{ b.nome }} (R$ {{ b.valor | number:'1.2-2' }})</option>
          </select>
          <div *ngIf="form.get('toId')?.invalid && form.get('toId')?.touched" class="error-message">
            Selecione o benefício de destino
          </div>
        </div>

        <div class="form-group">
          <label for="valor">Valor a Transferir *</label>
          <input 
            id="valor" 
            type="number" 
            step="0.01" 
            min="0.01"
            formControlName="valor"
            [class.error]="form.get('valor')?.invalid && form.get('valor')?.touched">
          <div *ngIf="form.get('valor')?.invalid && form.get('valor')?.touched" class="error-message">
            Valor deve ser maior que zero
          </div>
        </div>

        <button type="submit" class="btn btn-success" [disabled]="form.invalid || transferring">
          {{ transferring ? 'Transferindo...' : 'Transferir' }}
        </button>
      </form>
    </div>
  `
})
export class TransferenciaComponent implements OnInit {
  form!: FormGroup;
  beneficios: Beneficio[] = [];
  transferring = false;
  error: string | null = null;
  successMessage: string | null = null;

  constructor(
    private fb: FormBuilder,
    private service: BeneficioService
  ) {
    this.form = this.fb.group({
      fromId: ['', Validators.required],
      toId: ['', Validators.required],
      valor: [0, [Validators.required, Validators.min(0.01)]]
    }, {
      validators: this.differentBeneficiosValidator
    });
  }

  ngOnInit(): void {
    this.carregarBeneficios();
  }

  carregarBeneficios(): void {
    this.service.listar().subscribe({
      next: (data) => {
        this.beneficios = data.filter(b => b.ativo !== false);
      },
      error: (err) => {
        this.error = 'Erro ao carregar benefícios: ' + (err.error?.message || err.message);
      }
    });
  }

  differentBeneficiosValidator(group: FormGroup) {
    const fromId = group.get('fromId')?.value;
    const toId = group.get('toId')?.value;
    return fromId && toId && fromId === toId ? { sameBeneficio: true } : null;
  }

  transferir(): void {
    if (this.form.valid) {
      this.transferring = true;
      this.error = null;
      this.successMessage = null;

      const request: TransferenciaRequest = this.form.value;
      this.service.transferir(request).subscribe({
        next: (response) => {
          this.successMessage = `Transferência de R$ ${request.valor.toFixed(2)} realizada com sucesso!`;
          this.form.reset();
          this.carregarBeneficios();
          this.transferring = false;
        },
        error: (err) => {
          this.error = err.error?.message || err.error?.details || 'Erro ao realizar transferência';
          this.transferring = false;
        }
      });
    }
  }
}
