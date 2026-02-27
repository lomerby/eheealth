#!/usr/bin/env python3

"""
MLBB Drafting Simulator
Main entry point for the application
"""

import asyncio
import sys
from src.core.draft_simulator import DraftSimulator
from src.data.hero_database import HeroDatabase


async def main():
    """Initialize the application"""
    try:
        print('🎮 MLBB Drafting Simulator')
        print('================================')
        
        print('📚 Loading hero database...')
        hero_db = HeroDatabase()
        await hero_db.initialize()
        
        print('🎯 Initializing draft simulator...')
        simulator = DraftSimulator(hero_db)
        
        print('✅ Application ready!')
        print('📖 Check README.md for usage instructions')
        
        # TODO: Add CLI interface or web server
        await start_interactive_mode(simulator)
        
    except Exception as error:
        print(f'❌ Error initializing application: {error}')
        sys.exit(1)


async def start_interactive_mode(simulator: DraftSimulator):
    """Start interactive mode (placeholder)"""
    print('\n🚀 Interactive mode coming soon!')
    print('Features to be implemented:')
    print('- Hero selection interface')
    print('- Draft phase simulation')
    print('- Team composition analysis')
    print('- Counter-pick suggestions')
    
    # Demo the basic functionality
    print('\n🎯 Demo: Starting a draft session...')
    draft = simulator.start_draft()
    
    print('\n📊 Available heroes:')
    heroes = simulator.hero_db.get_all_heroes()
    for hero in heroes[:5]:  # Show first 5 heroes
        print(f"- {hero['name']} ({hero['role']}) - {hero['specialty']}")
        print(f"  Image: {hero['image_path']}")
    print(f"... and {len(heroes) - 5} more heroes")
    
    # Demo ban and pick
    print('\n🚫 Demo: Banning Tigreal for Team 1')
    simulator.ban_hero('tigreal', 1)
    
    print('✅ Demo: Picking Gusion for Team 2')
    simulator.pick_hero('gusion', 2)
    
    print('\n📋 Current draft state:')
    print(f"Team 1 bans: {draft['bans']['team1']}")
    print(f"Team 1 picks: {draft['picks']['team1']}")
    print(f"Team 2 bans: {draft['bans']['team2']}")
    print(f"Team 2 picks: {draft['picks']['team2']}")


if __name__ == '__main__':
    asyncio.run(main())