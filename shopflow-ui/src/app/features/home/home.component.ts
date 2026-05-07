import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Router } from '@angular/router';
import { CategoryService } from '../../core/services/category.service';
import { Category } from '../../core/models';
import { MatIconModule } from '@angular/material/icon';

import { CategoryBarComponent } from '../../shared/components/category-bar/category-bar.component';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, RouterModule, MatIconModule, CategoryBarComponent],
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent implements OnInit {
  categoryService = inject(CategoryService); // Service des catégories
  router = inject(Router); // Pour la navigation

  // Listes pour stocker les données affichées sur la page d'accueil
  categories: Category[] = []; // Liste des catégories
  loading = true; // État de chargement global

  ngOnInit() {
    this.loadData(); // Charge les données dès l'initialisation
  }

  /**
   * Charge les catégories pour la page d'accueil.
   */
  loadData() {
    this.loading = true;
    this.categoryService.getCategories().subscribe({
      next: (data: Category[]) => {
        this.categories = data;
        this.loading = false;
      },
      error: (err: any) => {
        console.error('Erreur lors du chargement des catégories', err);
        this.categories = [];
        this.loading = false;
      }
    });
  }

  getIconForCategory(name: string): string {
    const icons: any = {
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
