#!/usr/bin/env node

/**
 * MLBB Drafting Simulator
 * Main entry point for the application
 */

console.log('🎮 MLBB Drafting Simulator');
console.log('================================');

// Import required modules
const DraftSimulator = require('./core/DraftSimulator');
const HeroDatabase = require('./data/HeroDatabase');

/**
 * Initialize the application
 */
async function init() {
  try {
    console.log('📚 Loading hero database...');
    const heroDb = new HeroDatabase();
    await heroDb.initialize();
    
    console.log('🎯 Initializing draft simulator...');
    const simulator = new DraftSimulator(heroDb);
    
    console.log('✅ Application ready!');
    console.log('📖 Check README.md for usage instructions');
    
    // TODO: Add CLI interface or web server
    startInteractiveMode(simulator);
    
  } catch (error) {
    console.error('❌ Error initializing application:', error.message);
    process.exit(1);
  }
}

/**
 * Start interactive mode (placeholder)
 */
function startInteractiveMode(simulator) {
  console.log('\n🚀 Interactive mode coming soon!');
  console.log('Features to be implemented:');
  console.log('- Hero selection interface');
  console.log('- Draft phase simulation');
  console.log('- Team composition analysis');
  console.log('- Counter-pick suggestions');
}

// Start the application
if (require.main === module) {
  init();
}

module.exports = { init };