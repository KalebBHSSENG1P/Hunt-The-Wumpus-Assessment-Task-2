import pygame, random, time, sys

##############################################################################
#                             Functions Area                                 #
##############################################################################
def draw_room( pos, screen):
      """ Draws the room in the back buffer
      """
      x = 0
      y = 1
      exits = cave[player_pos]
      screen.fill(BLACK) # paint the background in black

      # draw the room circle in brown
      circle_radius = int((SCREEN_WIDTH // 2) * 0.75)
      pygame.draw.circle(screen, BROWN, (SCREEN_WIDTH // 2, SCREEN_HEIGHT // 2), circle_radius, 0)

      # next draw all exits from the room
      if exits[LEFT] > 0:
            left = 0
            top = SCREEN_HEIGHT // 2.40
            pygame.draw.rect(screen, BROWN, ( (left, top), (SCREEN_WIDTH // 4,80)), 0)
      if exits[RIGHT] > 0:
            # draw right exit
            left = SCREEN_WIDTH - (SCREEN_WIDTH // 4)
            top = SCREEN_HEIGHT // 2.40
            pygame.draw.rect(screen, BROWN, ( (left, top), (SCREEN_WIDTH // 4)), 0)
      if exits[UP] > 0:
            # draw top exit
            left = SCREEN_WIDTH // 2.40
            top = 0
            pygame.draw.rect(screen, BROWN, ( (left, top), (80, SCREEN_WIDTH // 4)), 0)
      if exits[DOWN] > 0:
            # draw bottom exit
            left = SCREEN_WIDTH // 2.40
            top = SCREEN_HEIGHT - (SCREEN_HEIGHT // 4)
            pygame.draw.rect(screen, BROWN, ((left, top), (80, SCREEN_HEIGHT // 4)), 0)
      
      # draw text
      y_text_pos = 0 # keeps track of the next y position on screen to draw text
      pos_text = font.render("POS:"+str(player_pos), 1, (0, 255, 64))
      screen.blit(pos_text, (0, y_text_pos))

def check_pygame_events():
      global player_pos
      event = pygame.event.poll()
      if event.type == pygame.QUIT:
            pygame.quit()
            sys.exit()
      elif event.type == pygame.KEYDOWN:
            if event.key == pygame.K_ESCAPE:
                  pygame.quit()
                  sys.exit()
            elif event.key == pygame.K_LEFT:
                  if cave[player_pos][LEFT] > 0:
                        player_pos=cave[player_pos][LEFT]
            elif event.key == pygame.K_RIGHT:      
                  if cave[player_pos][RIGHT] > 0:
                        player_pos=cave[player_pos][RIGHT]
            elif event.key == pygame.K_UP:      
                  if cave[player_pos][UP] > 0:
                        player_pos=cave[player_pos][UP]
            elif event.key == pygame.K_DOWN:      
                  if cave[player_pos][DOWN] > 0:
                        player_pos=cave[player_pos][DOWN]

def populate_cave():
      global player_pos, wumpus_pos

      # place the player in a random room
      player_pos = random.randint(1,20) 

      # place the wumpus somewhere in the cave
      place_wumpus()

      # place bats in random rooms
      for bat in range(0, NUM_BATS):
            place_bat()
      
      # place pits in random rooms
      for pit in range(0, NUM_PITS):
            place_pit()

      # place arrows in random rooms
      for arrow in range(0, NUM_ARROWS):
            place_arrow()

      print ("Player at: "+str(player_pos))
      print ("Wumpus at: "+str(wumpus_pos))
      print ("Bats at: "+str(bats_list))
      print ("Pits at: "+str(pits_list))
      print ("Arrows at: "+str(arrows_list))

def place_wumpus():
      global player_pos, wumpus_pos
      wumpus_pos = player_pos
      while (wumpus_pos == player_pos):
            wumpus_pos = random.randint(1, 20)

def place_bats():
      # place bats in random rooms not populated with the player, wumpus or any other bat
      bat_pos = player_pos
      while bat_pos == player_pos or (bat_pos in bats_list) or (bat_pos == wumpus_pos) or (bat_pos in pits_list):
            bat_pos = random.randint(1, 20)
      bats_list.append(bat_pos)

def place_pits():
      pit_pos = player_pos
      while (pit_pos == player_pos) or (pit_pos in bats_list) or (pit_pos == wumpus_pos) or (pit_pos in pits_list):
            pit_pos = random.randint(1, 20)
      pits_list.append(pit_pos)

def place_arrow():
      arrow_pos = player_pos
      while (arrow_pos == player_pos) or (arrow_pos in bats_list) or (arrow_pos == wumpus_pos) or (arrow_pos in pits_list) or (arrow_pos in arrows_list):
            arrow_pos = random.randint(1, 20)
      arrows_list.append(arrow_pos)

def print_instructions():
        print (
            '''
                                Hunt the Wumpus!
    This is the game of "Hunt the Wumpus". You have been cast into a
    dark 20 room cave with a fearsome wumpus. This cade is shaped like a
    dodecahedron and the only way out is to kill the wumpus. To that end
    you have a bow with one arrow. You might find more arrows from unlucky
    past wumpus victims in the cave. There are other dangers in the cave,
    specifically bats and bottomless pits.

        - If you run out of arrows you die.
        - If you end up in the same room with the wumpus you die.
        - If you fall into a bottomless pit you die.
        - If you end up in a room with bats they will pick you up
        and deposit you in a random location.

    If you are near the wumpus you will see bloodstains on the walls.
    If you are near bats you will hear them and if you are near a bottomless
    pit you will feel the air flowing down it.

    Use the arrow keys to move. Press the <SHIFT> key and an arrow key to
    fire yor arrow.
        '''
        )

def reset_game():
      global num_arrows
      # populate_cave()
      num_arrows = 1

def game_over():
      global screen
      time.sleep(1.0)
      screen.fill(RED)
      text=font.render(message, 1, (0, 255, 64))
      textrect = text.get_rect()
      textrect.centerx = screen.get_rect().centerx
      textrect.centery = screen.get_rect().centery
      screen.blit(text, textrect)
      pygame.display.flip()
      time.sleep(2.5)
      print (message)
      pygame.quit()
      sys.exit()

##############################################################################
#                        Globals and Constants area                          #
##############################################################################
# Width and height dimensions of screen
SCREEN_WIDTH = SCREEN_HEIGHT = 1000

# Number of bats, pits and arrows when playing
## increase the number of bats and pits to make the game harder
## increase the number of arrows to make the game easier
NUM_BATS = 3
NUM_PITS = 3
NUM_ARROWS = 0

player_pos = 0 # tracks position of the player in the gmae
wumpus_pos = 0 # tracks position of the wumpus in the game
num_arrows = 1 # starting number of arrows
mobile_wumpus = False # Set this boolean to true to allow the wumpus to move
wumpus_move_chance = 50

# constants to control direction
UP = 0
DOWN = 1
LEFT = 2
RIGHT = 3

# colour definitions
BROWN = 193,154,107
BLACK = 0,0,0
RED = 138,7,7

cave = {
    1: [0,8,2,5], 2: [0,10,3,1], 3: [0,12,4,2], 4: [0,14,5,3],
    5: [0,6,1,4], 6: [5,0,7,15], 7: [0,17,8,6], 8: [1,0,9,7],
    9: [0,18,10,8], 10: [2,0,11,9], 11: [0,19,12,10], 12: [3,0,13,11],
    13: [0,20,14,12], 14: [4,0,15,13], 15: [0,16,6,14], 16: [15,0,17,20],
    17: [7,0,18,16], 18: [9,0,19,17], 19: [11,0,20,18], 20: [13,0,16,19]
}

bats_list = []
pits_list = []
arrows_list = []

##############################################################################
#                            Initialisations Area                            #
##############################################################################

print_instructions()
input("Press <ENTER> to begin.")
pygame.init()
screen = pygame.display.set_mode( (SCREEN_WIDTH, SCREEN_WIDTH), pygame.DOUBLEBUF | pygame.HWSURFACE )
pygame.display.set_caption("Hunt the Wumpus")

# load game assets
bat_img = pygame.image.load("images/bat.png")
player_img = pygame.image.load("images/player.png")
wumpus_img = pygame.image.load("images/wumpus.png")
arrow_img = pygame.image.load("images/arrow.png")

# load fonts
font = pygame.font.Font(None, 36)

# Get initial game settings
reset_game()

##############################################################################
#                                Main Game Loop                              #
##############################################################################

while True:
    check_pygame_events()
    draw_room(player_pos, screen)
    pygame.display.flip()
    check_room(player_pos)