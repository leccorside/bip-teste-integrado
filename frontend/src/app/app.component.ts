import { Component } from '@angular/core';
import { RouterOutlet, RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, RouterModule, CommonModule],
  template: `
    <div class="container">
      <header>
        <h1>🏦 Sistema de Benefícios</h1>
        <nav>
          <a routerLink="/beneficios" routerLinkActive="active">Lista de Benefícios</a>
          <a routerLink="/beneficios/novo" routerLinkActive="active">Novo Benefício</a>
          <a routerLink="/transferencia" routerLinkActive="active">Transferência</a>
        </nav>
      </header>
      <main>
        <router-outlet></router-outlet>
      </main>
    </div>
  `,
  styles: [`
    header {
      margin-bottom: 30px;
      padding-bottom: 20px;
      border-bottom: 2px solid #667eea;

      h1 {
        color: #667eea;
        margin-bottom: 15px;
      }

      nav {
        display: flex;
        gap: 20px;

        a {
          text-decoration: none;
          color: #555;
          padding: 8px 16px;
          border-radius: 5px;
          transition: all 0.3s ease;

          &:hover {
            background: #f0f0f0;
            color: #667eea;
          }

          &.active {
            background: #667eea;
            color: white;
          }
        }
      }
    }
  `]
})
export class AppComponent {
  title = 'Sistema de Benefícios';
}
