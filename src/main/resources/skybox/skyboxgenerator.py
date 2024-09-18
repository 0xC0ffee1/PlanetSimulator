import pygame

filename = input("Enter image: ")

image = pygame.image.load(filename)

front = image.subsurface(1024, 1024, 1024, 1024)
left = image.subsurface(0, 1024, 1024, 1024)
right = image.subsurface(1024*2, 1024, 1024, 1024)
top = image.subsurface(1024, 0, 1024, 1024)
back = image.subsurface(1024*3, 1024, 1024, 1024)
bottom = image.subsurface(1024, 1024*2, 1024, 1024)

pygame.image.save(front, "front2.png")
pygame.image.save(left, "left2.png")
pygame.image.save(right, "right2.png")
pygame.image.save(top, "top2.png")
pygame.image.save(back, "back2.png")
pygame.image.save(bottom, "bottom2.png")
