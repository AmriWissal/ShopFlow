import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { DashboardService } from '../../../core/services/dashboard.service';
import { MatIconModule } from '@angular/material/icon';
import { ChartComponent } from '../../../shared/components/chart/chart.component';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, RouterModule, MatIconModule, ChartComponent],
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css']
})
export class DashboardComponent implements OnInit {
  stats: any = {
    totalOrders: 0,
    totalProducts: 0,
    totalUsers: 0,
    totalRevenue: 0,
    recentOrders: [],
    lowStock: [],
    chartData: [],
    categoryData: [],
    statusData: []
  }; // Stocke les statistiques globales (revenus, commandes, etc.)
  loading = true; // État de chargement

  constructor(private dashboardService: DashboardService) {}

  ngOnInit(): void {
    this.loadStats(); // Charge les données globales au démarrage
  }

  /**
   * Appelle le service pour récupérer les statistiques globales du système.
   */
  private loadStats(): void {
    this.dashboardService.getAdminStats().subscribe({
      next: (data) => {
        this.stats = data;
        
        // Données de secours (Mocks) pour les graphiques si la DB est vide
        if (!this.stats.chartData || this.stats.chartData.length === 0) {
          this.stats.chartData = [['Jan', 1200], ['Feb', 2100], ['Mar', 1800], ['Apr', 2400], ['May', 2900]];
        }
        if (!this.stats.categoryData || this.stats.categoryData.length === 0) {
          this.stats.categoryData = [['Légumes', 45], ['Fruits', 30], ['Épicerie', 25]];
        }
        if (!this.stats.statusData || this.stats.statusData.length === 0) {
          this.stats.statusData = [['Livrées', 60], ['En cours', 25], ['Annulées', 15]];
        }

        this.loading = false;
      },
      error: (err) => {
        console.error('Erreur lors du chargement des stats admin', err);
        this.loading = false;
      }
    });
  }
}