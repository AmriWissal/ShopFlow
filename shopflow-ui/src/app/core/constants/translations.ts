/**
 * Traductions françaises pour l'application ShopFlow
 * Toutes les chaînes de caractères de l'interface utilisateur
 */

export const TRANSLATIONS = {
  // Navigation & Menu
  nav: {
    home: 'Accueil',
    products: 'Produits',
    categories: 'Catégories',
    cart: 'Panier',
    profile: 'Profil',
    login: 'Connexion',
    register: 'Inscription',
    logout: 'Déconnexion',
    dashboard: 'Tableau de bord',
    myOrders: 'Mes commandes',
    myProducts: 'Mes produits',
    settings: 'Paramètres'
  },

  // Authentification
  auth: {
    login: 'Connexion',
    register: 'Inscription',
    email: 'Adresse email',
    password: 'Mot de passe',
    confirmPassword: 'Confirmer le mot de passe',
    firstName: 'Prénom',
    lastName: 'Nom',
    forgotPassword: 'Mot de passe oublié ?',
    noAccount: 'Pas encore de compte ?',
    hasAccount: 'Vous avez déjà un compte ?',
    signIn: 'Se connecter',
    signUp: 'S\'inscrire',
    role: 'Type de compte',
    customer: 'Client',
    seller: 'Vendeur',
    loginSuccess: 'Connexion réussie',
    registerSuccess: 'Inscription réussie',
    loginError: 'Erreur de connexion',
    registerError: 'Erreur lors de l\'inscription'
  },

  // Produits
  product: {
    product: 'Produit',
    products: 'Produits',
    name: 'Nom du produit',
    description: 'Description',
    price: 'Prix',
    promoPrice: 'Prix promotionnel',
    stock: 'Stock disponible',
    category: 'Catégorie',
    categories: 'Catégories',
    images: 'Images',
    addProduct: 'Ajouter un produit',
    editProduct: 'Modifier le produit',
    deleteProduct: 'Supprimer le produit',
    viewDetails: 'Voir les détails',
    addToCart: 'Ajouter au panier',
    outOfStock: 'Rupture de stock',
    inStock: 'En stock',
    newProduct: 'Nouveau produit',
    topSelling: 'Meilleures ventes',
    onSale: 'En promotion',
    searchProducts: 'Rechercher des produits...',
    noProducts: 'Aucun produit trouvé',
    productAdded: 'Produit ajouté au panier',
    productUpdated: 'Produit mis à jour',
    productDeleted: 'Produit supprimé'
  },

  // Panier
  cart: {
    cart: 'Panier',
    myCart: 'Mon panier',
    emptyCart: 'Votre panier est vide',
    continueShopping: 'Continuer mes achats',
    checkout: 'Commander',
    quantity: 'Quantité',
    subtotal: 'Sous-total',
    total: 'Total',
    remove: 'Retirer',
    update: 'Mettre à jour',
    applyCoupon: 'Appliquer un coupon',
    couponCode: 'Code promo',
    discount: 'Réduction',
    shippingFees: 'Frais de livraison',
    freeShipping: 'Livraison gratuite',
    itemAdded: 'Article ajouté au panier',
    itemRemoved: 'Article retiré du panier',
    itemUpdated: 'Panier mis à jour'
  },

  // Commandes
  order: {
    order: 'Commande',
    orders: 'Commandes',
    myOrders: 'Mes commandes',
    orderNumber: 'N° de commande',
    orderDate: 'Date de commande',
    orderStatus: 'Statut',
    orderTotal: 'Montant total',
    shippingAddress: 'Adresse de livraison',
    billingAddress: 'Adresse de facturation',
    orderDetails: 'Détails de la commande',
    trackOrder: 'Suivre ma commande',
    cancelOrder: 'Annuler la commande',
    orderPlaced: 'Commande passée avec succès',
    noOrders: 'Aucune commande',
    status: {
      PENDING: 'En attente',
      CONFIRMED: 'Confirmée',
      PROCESSING: 'En préparation',
      SHIPPED: 'Expédiée',
      DELIVERED: 'Livrée',
      CANCELLED: 'Annulée'
    }
  },

  // Catégories
  category: {
    category: 'Catégorie',
    categories: 'Catégories',
    allCategories: 'Toutes les catégories',
    addCategory: 'Ajouter une catégorie',
    editCategory: 'Modifier la catégorie',
    deleteCategory: 'Supprimer la catégorie',
    categoryName: 'Nom de la catégorie',
    parentCategory: 'Catégorie parente',
    subCategories: 'Sous-catégories',
    noCategories: 'Aucune catégorie'
  },

  // Profil
  profile: {
    profile: 'Profil',
    myProfile: 'Mon profil',
    editProfile: 'Modifier le profil',
    personalInfo: 'Informations personnelles',
    addresses: 'Adresses',
    addAddress: 'Ajouter une adresse',
    editAddress: 'Modifier l\'adresse',
    deleteAddress: 'Supprimer l\'adresse',
    defaultAddress: 'Adresse par défaut',
    street: 'Rue',
    city: 'Ville',
    postalCode: 'Code postal',
    country: 'Pays',
    phone: 'Téléphone',
    profileUpdated: 'Profil mis à jour'
  },

  // Dashboard
  dashboard: {
    dashboard: 'Tableau de bord',
    statistics: 'Statistiques',
    totalSales: 'Ventes totales',
    totalOrders: 'Commandes totales',
    totalProducts: 'Produits totaux',
    totalCustomers: 'Clients totaux',
    recentOrders: 'Commandes récentes',
    topProducts: 'Produits populaires',
    revenue: 'Chiffre d\'affaires',
    thisMonth: 'Ce mois-ci',
    thisWeek: 'Cette semaine',
    today: 'Aujourd\'hui'
  },

  // Actions communes
  actions: {
    save: 'Enregistrer',
    cancel: 'Annuler',
    delete: 'Supprimer',
    edit: 'Modifier',
    add: 'Ajouter',
    create: 'Créer',
    update: 'Mettre à jour',
    search: 'Rechercher',
    filter: 'Filtrer',
    sort: 'Trier',
    view: 'Voir',
    back: 'Retour',
    next: 'Suivant',
    previous: 'Précédent',
    confirm: 'Confirmer',
    close: 'Fermer',
    submit: 'Soumettre',
    reset: 'Réinitialiser',
    apply: 'Appliquer',
    clear: 'Effacer',
    download: 'Télécharger',
    upload: 'Téléverser',
    export: 'Exporter',
    import: 'Importer',
    print: 'Imprimer',
    share: 'Partager',
    copy: 'Copier',
    refresh: 'Actualiser'
  },

  // Messages
  messages: {
    success: 'Opération réussie',
    error: 'Une erreur est survenue',
    loading: 'Chargement...',
    noData: 'Aucune donnée disponible',
    confirmDelete: 'Êtes-vous sûr de vouloir supprimer cet élément ?',
    confirmAction: 'Êtes-vous sûr de vouloir effectuer cette action ?',
    saved: 'Enregistré avec succès',
    deleted: 'Supprimé avec succès',
    updated: 'Mis à jour avec succès',
    created: 'Créé avec succès',
    unauthorized: 'Accès non autorisé',
    forbidden: 'Accès refusé',
    notFound: 'Ressource introuvable',
    serverError: 'Erreur serveur',
    networkError: 'Erreur de connexion',
    validationError: 'Erreur de validation',
    requiredField: 'Ce champ est obligatoire',
    invalidEmail: 'Adresse email invalide',
    invalidFormat: 'Format invalide',
    passwordMismatch: 'Les mots de passe ne correspondent pas',
    minLength: 'Longueur minimale : {min} caractères',
    maxLength: 'Longueur maximale : {max} caractères'
  },

  // Filtres
  filters: {
    filters: 'Filtres',
    priceRange: 'Fourchette de prix',
    minPrice: 'Prix minimum',
    maxPrice: 'Prix maximum',
    sortBy: 'Trier par',
    sortByName: 'Nom',
    sortByPrice: 'Prix',
    sortByDate: 'Date',
    sortByPopularity: 'Popularité',
    ascending: 'Croissant',
    descending: 'Décroissant',
    showAll: 'Tout afficher',
    promoOnly: 'Promotions uniquement',
    inStockOnly: 'En stock uniquement'
  },

  // Pagination
  pagination: {
    page: 'Page',
    of: 'sur',
    items: 'éléments',
    itemsPerPage: 'Éléments par page',
    showing: 'Affichage de',
    to: 'à',
    first: 'Premier',
    last: 'Dernier',
    noResults: 'Aucun résultat'
  },

  // Devise & Format
  currency: {
    symbol: 'DT',
    format: '{amount} DT'
  },

  // Tunisie spécifique
  tunisia: {
    country: 'Tunisie',
    cities: 'Villes de Tunisie',
    selectCity: 'Sélectionner une ville',
    shippingInfo: 'Livraison gratuite à partir de 100 DT',
    defaultShipping: 'Frais de livraison : 7 DT'
  },

  // Avis & Notes
  reviews: {
    reviews: 'Avis',
    addReview: 'Ajouter un avis',
    rating: 'Note',
    comment: 'Commentaire',
    noReviews: 'Aucun avis pour le moment',
    writeReview: 'Rédiger un avis',
    yourRating: 'Votre note',
    yourComment: 'Votre commentaire',
    submitReview: 'Publier l\'avis',
    averageRating: 'Note moyenne',
    totalReviews: 'avis'
  },

  // Coupons
  coupon: {
    coupon: 'Coupon',
    coupons: 'Coupons',
    couponCode: 'Code promo',
    discount: 'Réduction',
    validUntil: 'Valable jusqu\'au',
    minAmount: 'Montant minimum',
    maxUses: 'Utilisations maximum',
    addCoupon: 'Ajouter un coupon',
    applyCoupon: 'Appliquer',
    removeCoupon: 'Retirer le coupon',
    couponApplied: 'Coupon appliqué',
    couponInvalid: 'Coupon invalide',
    couponExpired: 'Coupon expiré'
  }
};

// Fonction helper pour obtenir une traduction
export function t(key: string): string {
  const keys = key.split('.');
  let value: any = TRANSLATIONS;
  
  for (const k of keys) {
    value = value?.[k];
    if (value === undefined) {
      console.warn(`Translation key not found: ${key}`);
      return key;
    }
  }
  
  return value;
}

// Fonction pour formater un prix en DT
export function formatPrice(amount: number): string {
  return `${amount.toFixed(3)} DT`;
}

// Fonction pour formater une date en français
export function formatDate(date: Date | string): string {
  const d = typeof date === 'string' ? new Date(date) : date;
  return d.toLocaleDateString('fr-TN', {
    year: 'numeric',
    month: 'long',
    day: 'numeric'
  });
}
