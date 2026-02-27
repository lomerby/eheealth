/**
 * HeroDatabase - Manages MLBB hero data and metadata
 */

class HeroDatabase {
  constructor() {
    this.heroes = new Map();
    this.roles = ['Tank', 'Fighter', 'Assassin', 'Mage', 'Marksman', 'Support'];
  }

  /**
   * Initialize the hero database with MLBB heroes
   */
  async initialize() {
    console.log('📊 Loading MLBB heroes...');
    
    // Sample heroes data - in a real implementation, this would come from a JSON file or API
    const heroesData = [
      { id: 'tigreal', name: 'Tigreal', role: 'Tank', specialty: 'Crowd Control' },
      { id: 'alucard', name: 'Alucard', role: 'Fighter', specialty: 'Damage' },
      { id: 'gusion', name: 'Gusion', role: 'Assassin', specialty: 'Burst' },
      { id: 'kagura', name: 'Kagura', role: 'Mage', specialty: 'Burst/Crowd Control' },
      { id: 'granger', name: 'Granger', role: 'Marksman', specialty: 'Damage' },
      { id: 'angela', name: 'Angela', role: 'Support', specialty: 'Crowd Control/Support' },
      { id: 'kaja', name: 'Kaja', role: 'Support', specialty: 'Crowd Control' },
      { id: 'claude', name: 'Claude', role: 'Marksman', specialty: 'Damage' },
      { id: 'esmeralda', name: 'Esmeralda', role: 'Tank', specialty: 'Magic Damage' },
      { id: 'ling', name: 'Ling', role: 'Assassin', specialty: 'Mobility' }
    ];

    heroesData.forEach(hero => {
      this.heroes.set(hero.id, {
        ...hero,
        winRate: Math.random() * 0.2 + 0.45, // Random win rate 45-65%
        banRate: Math.random() * 0.3, // Random ban rate 0-30%
        pickRate: Math.random() * 0.4 + 0.1, // Random pick rate 10-50%
        counters: [], // TODO: Add counter relationships
        synergies: [] // TODO: Add synergy relationships
      });
    });

    console.log(`✅ Loaded ${this.heroes.size} heroes`);
  }

  /**
   * Get all heroes
   */
  getAllHeroes() {
    return Array.from(this.heroes.values());
  }

  /**
   * Get hero by ID
   */
  getHero(heroId) {
    return this.heroes.get(heroId);
  }

  /**
   * Get heroes by role
   */
  getHeroesByRole(role) {
    return this.getAllHeroes().filter(hero => hero.role === role);
  }

  /**
   * Get available roles
   */
  getRoles() {
    return this.roles;
  }

  /**
   * Search heroes by name
   */
  searchHeroes(query) {
    const lowercaseQuery = query.toLowerCase();
    return this.getAllHeroes().filter(hero => 
      hero.name.toLowerCase().includes(lowercaseQuery)
    );
  }
}

module.exports = HeroDatabase;