import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatIconModule } from '@angular/material/icon';
import { Category } from '../../../core/models';

@Component({
  selector: 'app-category-bar',
  standalone: true,
  imports: [CommonModule, MatIconModule],
  template: `
    <!-- Conteneur Sticky avec Glassmorphism -->
    <div class="sticky top-24 z-30 mb-8 px-2">
      <div class="bg-white/70 backdrop-blur-xl border border-white/40 rounded-[2rem] p-2 shadow-[0_8px_32px_rgba(0,0,0,0.04)]">
        
        <div class="flex items-center gap-3 overflow-x-auto pb-1 scrollbar-hide">
          
          <!-- Bouton "Tous les produits" -->
          <button (click)="selectCategory('')"
            [class]="selectedId === '' 
              ? 'bg-green-500 text-white shadow-lg shadow-green-200' 
              : 'bg-white text-slate-500 hover:border-green-200 hover:text-green-600'"
            class="flex items-center gap-3 px-6 py-3 rounded-2xl border border-slate-100 transition-all duration-300 shrink-0 group">
            
            <div [class]="selectedId === '' ? 'bg-white/20' : 'bg-slate-50 group-hover:bg-green-50'" 
                 class="w-8 h-8 rounded-xl flex items-center justify-center transition-colors">
              <mat-icon class="text-[18px] h-5 w-5">dashboard</mat-icon>
            </div>
            <span class="text-sm font-bold tracking-tight">Tous les produits</span>
          </button>

          <!-- Séparateur vertical -->
          <div class="w-px h-8 bg-slate-200/60 mx-1 shrink-0"></div>

          <!-- Catégories dynamiques -->
          <button *ngFor="let cat of categories"
            (click)="selectCategory(cat.id.toString())"
            [class]="selectedId === cat.id.toString() 
              ? 'bg-green-500 text-white shadow-lg shadow-green-200' 
              : 'bg-white text-slate-500 hover:border-green-200 hover:text-green-600'"
            class="flex items-center gap-3 px-5 py-3 rounded-2xl border border-slate-100 transition-all duration-300 shrink-0 group">
            
            <div [class]="selectedId === cat.id.toString() ? 'bg-white/20' : 'bg-slate-50 group-hover:bg-green-50'"
                 class="w-8 h-8 rounded-xl flex items-center justify-center transition-colors">
              <mat-icon class="text-[18px] h-5 w-5">{{ getIcon(cat.name) }}</mat-icon>
            </div>
            <span class="text-sm font-bold tracking-tight">{{ cat.name }}</span>
          </button>

        </div>
      </div>
    </div>
  `,
  styles: [`
    /* Force la disparition de la scrollbar sans casser le flex */
    .scrollbar-hide::-webkit-scrollbar { display: none; }
    .scrollbar-hide { -ms-overflow-style: none; scrollbar-width: none; }
    
    /* Animation fluide pour le changement de couleur */
    button { transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1); }
  `]
})
export class CategoryBarComponent {
  @Input() categories: Category[] = [];
  @Input() selectedId: string = '';
  @Output() categorySelected = new EventEmitter<string>();

  selectCategory(id: string) {
    this.categorySelected.emit(id);
  }

  getIcon(name: string): string {
    const icons: Record<string, string> = {
      'Légumes frais': 'eco',
      'Fruits de saison': 'restaurant_menu',
      'Huiles & Olives': 'oil_barrel',
      'Produits laitiers': 'egg',
      'Boulangerie & Traditionnel': 'bakery_dining',
      'Viandes & Volailles': 'kebab_dining',
      'Produits de la mer': 'set_meal',
      'Épices & Condiments': 'grain',
      'Produits artisanaux': 'auto_awesome',
      'Electronics': 'devices',
      'Fashion': 'checkroom',
      'Home': 'home',
      'Beauty': 'auto_fix_high',
      'Sports': 'fitness_center',
      'Électronique': 'devices',
      'Mode': 'checkroom',
      'Accessoires': 'watch'
    };
    return icons[name] || 'category';
  }
}